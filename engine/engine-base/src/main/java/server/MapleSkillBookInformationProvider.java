package server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import client.MapleCharacter;
import database.DatabaseConnection;
import database.provider.ReactorDropProvider;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.LoggerOriginator;
import tools.LoggerUtil;

public class MapleSkillBookInformationProvider {
   private final static MapleSkillBookInformationProvider instance = new MapleSkillBookInformationProvider();
   protected static Map<Integer, SkillBookEntry> foundSkillBooks = new HashMap<>();
   private static String rootDirectory = ".";
   private static int skillBookMinItemId = 2280000;
   private static int skillBookMaxItemId = 2300000;  // exclusively
   private static Set<Integer> questSkills = new HashSet<>();

   static {
      loadSkillBooks();
   }

   public static MapleSkillBookInformationProvider getInstance() {
      return instance;
   }

   private static boolean is4thJobSkill(int itemId) {
      return itemId / 10000 % 10 == 2;
   }

   public static boolean isSkillBook(int itemId) {
      return itemId >= skillBookMinItemId && itemId < skillBookMaxItemId;
   }

   private static boolean isQuestBook(int itemId) {
      return itemId >= 4001107 && itemId <= 4001114 || itemId >= 4161015 && itemId <= 4161023;
   }

   private static int fetchQuestBook(MapleData checkData, String quest) {
      MapleData questStartData = checkData.getChildByPath(quest).getChildByPath("0");

      MapleData startReqItemData = questStartData.getChildByPath("item");
      if (startReqItemData != null) {
         for (MapleData itemData : startReqItemData.getChildren()) {
            int itemId = MapleDataTool.getInt("id", itemData, 0);
            if (isQuestBook(itemId)) {
               return itemId;
            }
         }
      }

      MapleData startReqQuestData = questStartData.getChildByPath("quest");
      if (startReqQuestData != null) {
         Set<Integer> reqQuests = new HashSet<>();

         for (MapleData questStatusData : startReqQuestData.getChildren()) {
            int reqQuest = MapleDataTool.getInt("id", questStatusData, 0);
            if (reqQuest > 0) {
               reqQuests.add(reqQuest);
            }
         }

         for (Integer reqQuest : reqQuests) {
            int book = fetchQuestBook(checkData, Integer.toString(reqQuest));
            if (book > -1) {
               return book;
            }
         }
      }

      return -1;
   }

   private static void fetchSkillBooksFromQuests() {
      MapleDataProvider questDataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/" + "Quest.wz"));
      MapleData actData = questDataProvider.getData("Act.img");
      MapleData checkData = questDataProvider.getData("Check.img");

      for (MapleData questData : actData.getChildren()) {
         for (MapleData questStatusData : questData.getChildren()) {
            for (MapleData questNodeData : questStatusData.getChildren()) {
               String actNodeName = questNodeData.getName();
               if (actNodeName.contentEquals("item")) {
                  for (MapleData questItemData : questNodeData.getChildren()) {
                     int itemId = MapleDataTool.getInt("id", questItemData, 0);
                     int itemCount = MapleDataTool.getInt("count", questItemData, 0);

                     if (isSkillBook(itemId) && itemCount > 0) {
                        int questBook = fetchQuestBook(checkData, questData.getName());
                        if (questBook < 0) {
                           foundSkillBooks.put(itemId, SkillBookEntry.QUEST);
                        } else {
                           foundSkillBooks.put(itemId, SkillBookEntry.QUEST_BOOK);
                        }
                     }
                  }
               } else if (actNodeName.contentEquals("skill")) {
                  for (MapleData questSkillData : questNodeData.getChildren()) {
                     int skillId = MapleDataTool.getInt("id", questSkillData, 0);
                     if (is4thJobSkill(skillId)) {
                        foundSkillBooks.put(-skillId, SkillBookEntry.QUEST_REWARD);
                     }
                  }
               }
            }
         }
      }
   }

   private static void fetchSkillBooksFromReactors() {
      DatabaseConnection.getInstance().withConnection(connection ->
            ReactorDropProvider.getInstance().getDropIds(connection, skillBookMinItemId, skillBookMaxItemId)
                  .forEach(id -> foundSkillBooks.put(id, SkillBookEntry.REACTOR)));
   }

   private static void listFiles(String directoryName, ArrayList<File> files) {
      File directory = new File(directoryName);

      // get all the files from a directory
      File[] fList = directory.listFiles();
      for (File file : fList) {
         if (file.isFile()) {
            files.add(file);
         } else if (file.isDirectory()) {
            listFiles(file.getAbsolutePath(), files);
         }
      }
   }

   private static List<File> listFilesFromDirectoryRecursively(String directory) {
      ArrayList<File> files = new ArrayList<>();
      listFiles(directory, files);

      return files;
   }

   private static void filterScriptDirectorySearchMatchingData(String path) {
      for (File file : listFilesFromDirectoryRecursively(rootDirectory + "/" + path)) {
         if (file.getName().endsWith(".js")) {
            fileSearchMatchingData(file);
         }
      }
   }

   private static Set<Integer> foundMatchingDataOnFile(String fileContent) {
      Set<Integer> matches = new HashSet<>(4);

      Matcher searchM = Pattern.compile("22([89])[0-9]{4}").matcher(fileContent);
      int idx = 0;
      while (searchM.find(idx)) {
         idx = searchM.end();
         matches.add(Integer.valueOf(fileContent.substring(searchM.start(), idx)));
      }

      return matches;
   }

   static String readFileToString(File file, String encoding) throws IOException {
      Scanner scanner = new Scanner(file, encoding);
      String text = "";
      try {
         try {
            text = scanner.useDelimiter("\\A").next();
         } finally {
            scanner.close();
         }
      } catch (NoSuchElementException ignored) {
      }

      return text;
   }

   private static void fileSearchMatchingData(File file) {
      try {
         String fileContent = readFileToString(file, "UTF-8");

         Set<Integer> books = foundMatchingDataOnFile(fileContent);
         for (Integer i : books) {
            foundSkillBooks.put(i, SkillBookEntry.SCRIPT);
         }
      } catch (IOException ioe) {
         LoggerUtil.printError(LoggerOriginator.EXCEPTION, "Failed to read " + file.getName() + ".");
      }
   }

   private static void fetchSkillBooksFromScripts() {
      filterScriptDirectorySearchMatchingData("script/src/main/groovy");
   }

   private static void loadSkillBooks() {
      fetchSkillBooksFromQuests();
      fetchSkillBooksFromReactors();
      fetchSkillBooksFromScripts();
   }

   public SkillBookEntry getSkillBookAvailability(int itemId) {
      SkillBookEntry sbe = foundSkillBooks.get(itemId);
      return sbe != null ? sbe : SkillBookEntry.UNAVAILABLE;
   }

   public enum SkillBookEntry {
      UNAVAILABLE,
      QUEST,
      QUEST_BOOK,
      QUEST_REWARD,
      REACTOR,
      SCRIPT
   }

   public List<Integer> getTeachableSkills(MapleCharacter chr) {
      List<Integer> list = new ArrayList<>();

      for (Integer book : foundSkillBooks.keySet()) {
         if (book >= 0) {
            continue;
         }

         int skillId = -book;
         if (skillId / 10000 == chr.getJob().getId()) {
            if (chr.getMasterLevel(skillId) == 0) {
               list.add(-skillId);
            }
         }
      }

      return list;
   }
}
