package client.command.commands.gm2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.ThreadManager;
import tools.MessageBroadcaster;
import tools.exceptions.IdTypeNotSupportedException;
import tools.I18nMessage;

public class IdCommand extends Command {
   private final Map<String, String> handbookDirectory = new HashMap<>();
   private final Map<String, HashMap<String, String>> itemMap = new HashMap<>();

   {
      setDescription("");
   }

   public IdCommand() {
      handbookDirectory.put("map", "handbook/Map.txt");
      handbookDirectory.put("etc", "handbook/Etc.txt");
      handbookDirectory.put("npc", "handbook/NPC.txt");
      handbookDirectory.put("use", "handbook/Use.txt");
      handbookDirectory.put("weapon", "handbook/Equip/Weapon.txt"); // TODO add more into this
   }

   @Override
   public void execute(MapleClient client, final String[] params) {
      final MapleCharacter player = client.getPlayer();
      if (params.length < 2) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("ID_COMMAND_SYNTAX"));
         return;
      }
      final String queryItem = joinStringArr(Arrays.copyOfRange(params, 1, params.length), " ");
      MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("ID_COMMAND_BE_PATIENT"));
      Runnable queryRunnable = () -> {
         try {
            populateIdMap(params[0].toLowerCase());

            Map<String, String> resultList = fetchResults(itemMap.get(params[0]), queryItem);
            StringBuilder sb = new StringBuilder();

            if (resultList.size() > 0) {
               int count = 0;
               for (Map.Entry<String, String> entry : resultList.entrySet()) {
                  sb.append(String.format("Id for %s is: #b%s#k", entry.getKey(), entry.getValue())).append("\r\n");
                  if (++count > 100) {
                     break;
                  }
               }
               sb.append(String.format("Results found: #r%d#k | Returned: #b%d#k/100 | Refine search query to improve time.", resultList.size(), count)).append("\r\n");

               player.getAbstractPlayerInteraction().npcTalk(9010000, sb.toString());
            } else {
               MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("ID_COMMAND_ID_NOT_FOUND").with(queryItem, params[0]));
            }
         } catch (IdTypeNotSupportedException e) {
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("ID_COMMAND_NOT_SUPPORTED"));
         } catch (IOException e) {
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("ID_COMMAND_GENERIC_ERROR"));
         }
      };

      ThreadManager.getInstance().newTask(queryRunnable);
   }

   private void populateIdMap(String type) throws IdTypeNotSupportedException, IOException {
      if (!handbookDirectory.containsKey(type)) {
         throw new IdTypeNotSupportedException();
      }
      itemMap.put(type, new HashMap<>());
      BufferedReader reader = new BufferedReader(new FileReader(handbookDirectory.get(type)));
      String line;
      while ((line = reader.readLine()) != null) {
         String[] row = line.split(" - ", 2);
         if (row.length == 2) {
            itemMap.get(type).put(row[1].toLowerCase(), row[0]);
         }
      }
   }

   private String joinStringArr(String[] arr, String separator) {
      if (null == arr || 0 == arr.length) {
         return "";
      }
      StringBuilder sb = new StringBuilder(256);
      sb.append(arr[0]);
      for (int i = 1; i < arr.length; i++) {
         sb.append(separator).append(arr[i]);
      }
      return sb.toString();
   }

   private Map<String, String> fetchResults(Map<String, String> queryMap, String queryItem) {
      Map<String, String> results = new HashMap<>();
      for (String item : queryMap.keySet()) {
         if (item.contains(queryItem)) {
            results.put(item, queryMap.get(item));
         }
      }
      return results;
   }
}
