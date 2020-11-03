package server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import com.ms.qos.rest.SkillBookAttributes;
import com.ms.shared.rest.RestService;
import com.ms.shared.rest.UriBuilder;

import client.MapleCharacter;
import constants.ItemConstants;
import constants.SkillBookEntry;
import database.DatabaseConnection;
import database.provider.ReactorDropProvider;
import rest.DataContainer;

public class MapleSkillBookInformationProvider {
   private final static MapleSkillBookInformationProvider instance = new MapleSkillBookInformationProvider();
   protected static Map<Integer, SkillBookEntry> foundSkillBooks = new HashMap<>();
   private static String rootDirectory = ".";
   private static Set<Integer> questSkills = new HashSet<>();

   public static MapleSkillBookInformationProvider getInstance() {
      return instance;
   }

   private void fetchSkillBooksFromQuests() {
      UriBuilder.service(RestService.QUEST)
            .path("quests").path("items").path("skillBooks")
            .getRestClient(SkillBookAttributes.class)
            .getWithResponse()
            .result()
            .map(DataContainer::getDataAsList)
            .stream()
            .flatMap(Collection::stream)
            .forEach(result -> {
               int id = Integer.parseInt(result.getId());
               SkillBookEntry entry = SkillBookEntry.valueOf(result.getAttributes().getType());
               foundSkillBooks.put(id, entry);
            });
   }

   private void fetchSkillBooksFromReactors() {
      DatabaseConnection.getInstance().withConnection(connection ->
            ReactorDropProvider.getInstance()
                  .getDropIds(connection, ItemConstants.SKILL_BOOK_MIN_ITEM_ID, ItemConstants.SKILL_BOOK_MAX_ITEM_ID)
                  .forEach(id -> foundSkillBooks.put(id, SkillBookEntry.REACTOR)));
   }

   private void listFiles(String directoryName, ArrayList<File> files) {
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

   private List<File> listFilesFromDirectoryRecursively(String directory) {
      ArrayList<File> files = new ArrayList<>();
      listFiles(directory, files);

      return files;
   }

   private void filterScriptDirectorySearchMatchingData(String path) {
      for (File file : listFilesFromDirectoryRecursively(rootDirectory + "/" + path)) {
         if (file.getName().endsWith(".js")) {
            fileSearchMatchingData(file);
         }
      }
   }

   private Set<Integer> foundMatchingDataOnFile(String fileContent) {
      Set<Integer> matches = new HashSet<>(4);

      Matcher searchM = Pattern.compile("22([89])[0-9]{4}").matcher(fileContent);
      int idx = 0;
      while (searchM.find(idx)) {
         idx = searchM.end();
         matches.add(Integer.valueOf(fileContent.substring(searchM.start(), idx)));
      }

      return matches;
   }

   private String readFileToString(File file, String encoding) throws IOException {
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

   private void fileSearchMatchingData(File file) {
      try {
         String fileContent = readFileToString(file, "UTF-8");

         Set<Integer> books = foundMatchingDataOnFile(fileContent);
         for (Integer i : books) {
            foundSkillBooks.put(i, SkillBookEntry.SCRIPT);
         }
      } catch (IOException ioe) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, "Failed to read " + file.getName() + ".");
      }
   }

   private void fetchSkillBooksFromScripts() {
      filterScriptDirectorySearchMatchingData("script/src/main/groovy");
   }

   private void loadSkillBooks() {
      fetchSkillBooksFromQuests();
      fetchSkillBooksFromReactors();
      fetchSkillBooksFromScripts();
   }

   public Map<Integer, SkillBookEntry> getSkillBooks() {
      if (foundSkillBooks == null || foundSkillBooks.size() == 0) {
         loadSkillBooks();
      }
      return foundSkillBooks;
   }

   public SkillBookEntry getSkillBookAvailability(int itemId) {
      SkillBookEntry sbe = getSkillBooks().get(itemId);
      return sbe != null ? sbe : SkillBookEntry.UNAVAILABLE;
   }

   public List<Integer> getTeachableSkills(MapleCharacter chr) {
      List<Integer> list = new ArrayList<>();

      for (Integer book : getSkillBooks().keySet()) {
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
