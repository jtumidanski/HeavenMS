/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2018 RonanLana

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
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

import client.database.provider.ReactorDropProvider;
import provider.MapleData;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.DatabaseConnection;

/**
 * @author RonanLana
 */
public class MapleSkillbookInformationProvider {
   private final static MapleSkillbookInformationProvider instance = new MapleSkillbookInformationProvider();
   protected static Map<Integer, SkillBookEntry> foundSkillbooks = new HashMap<>();
   private static String rootDirectory = ".";
   private static int skillbookMinItemid = 2280000;
   private static int skillbookMaxItemid = 2300000;  // exclusively
   private static int currentItemid = 0;

   static {
      loadSkillbooks();
   }

   public static MapleSkillbookInformationProvider getInstance() {
      return instance;
   }

   public static boolean isSkillBook(int itemid) {
      return itemid >= skillbookMinItemid && itemid < skillbookMaxItemid;
   }

   private static void fetchSkillbooksFromQuests() {
      MapleData actData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/" + "Quest.wz")).getData("Act.img");

      for (MapleData questData : actData.getChildren()) {
         for (MapleData questStatusData : questData.getChildren()) {
            for (MapleData questNodeData : questStatusData.getChildren()) {
               if (questNodeData.getName().contentEquals("item")) {
                  for (MapleData questItemData : questNodeData.getChildren()) {
                     int itemid = MapleDataTool.getInt("id", questItemData, 0);
                     int itemcount = MapleDataTool.getInt("count", questItemData, 0);

                     if (isSkillBook(itemid) && itemcount > 0) {
                        foundSkillbooks.put(currentItemid, SkillBookEntry.QUEST);
                     }
                  }

                  break;
               }
            }
         }
      }
   }

   private static void fetchSkillbooksFromReactors() {
      DatabaseConnection.getInstance().withConnection(connection ->
            ReactorDropProvider.getInstance().getDropIds(connection, skillbookMinItemid, skillbookMaxItemid)
                  .forEach(id -> foundSkillbooks.put(id, SkillBookEntry.REACTOR)));
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

      Matcher searchM = Pattern.compile("22(8|9)[0-9]{4}").matcher(fileContent);
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
            foundSkillbooks.put(i, SkillBookEntry.SCRIPT);
         }
      } catch (IOException ioe) {
         System.out.println("Failed to read " + file.getName() + ".");
         ioe.printStackTrace();
      }
   }

   private static void fetchSkillbooksFromScripts() {
      filterScriptDirectorySearchMatchingData("script/src/main/groovy");
   }

   private static void loadSkillbooks() {
      fetchSkillbooksFromQuests();
      fetchSkillbooksFromReactors();
      fetchSkillbooksFromScripts();
   }

   public SkillBookEntry getSkillbookAvailability(int itemid) {
      SkillBookEntry sbe = foundSkillbooks.get(itemid);
      return sbe != null ? sbe : SkillBookEntry.UNAVAILABLE;
   }

   public enum SkillBookEntry {
      UNAVAILABLE,
      QUEST,
      REACTOR,
      SCRIPT
   }

}
