package server.loot;

import java.util.LinkedList;
import java.util.List;

import client.MapleCharacter;
import server.life.MapleMonsterInformationProvider;
import server.life.MonsterDropEntry;
import server.quest.MapleQuest;

public class MapleLootManager {

   private static boolean isRelevantDrop(MonsterDropEntry dropEntry, List<MapleCharacter> players, List<MapleLootInventory> playersInv) {
      int qStartAmount = 0, qCompleteAmount = 0;
      MapleQuest quest = MapleQuest.getInstance(dropEntry.questId());
      if (quest != null) {
         qStartAmount = quest.getStartItemAmountNeeded(dropEntry.itemId());
         qCompleteAmount = quest.getCompleteItemAmountNeeded(dropEntry.itemId());
      }

      //boolean restricted = MapleItemInformationProvider.getInstance().isPickupRestricted(dropEntry.itemId);
      for (int i = 0; i < players.size(); i++) {
         MapleLootInventory chrInv = playersInv.get(i);

         if (dropEntry.questId() > 0) {
            int qItemAmount, chrQuestStatus = players.get(i).getQuestStatus(dropEntry.questId());
            if (chrQuestStatus == 0) {
               qItemAmount = qStartAmount;
            } else if (chrQuestStatus != 1) {
               continue;
            } else {
               qItemAmount = qCompleteAmount;
            }

            int qItemStatus = chrInv.hasItem(dropEntry.itemId(), qItemAmount);
            if (qItemStatus == 2) {
               continue;
            } /*else if (restricted && qItemStatus == 1) {
                    continue;
                }*/
         } /*else if (restricted && chrInv.hasItem(dropEntry.itemId, 1) > 0) {
                continue;
            }*/

         return true;
      }

      return false;
   }

   public static List<MonsterDropEntry> retrieveRelevantDrops(int monsterId, List<MapleCharacter> players) {
      List<MonsterDropEntry> loots = MapleMonsterInformationProvider.getInstance().retrieveEffectiveDrop(monsterId);
      if (loots.isEmpty()) return loots;

      List<MapleLootInventory> playersInv = new LinkedList<>();
      for (MapleCharacter chr : players) {
         MapleLootInventory lootInv = new MapleLootInventory(chr);
         playersInv.add(lootInv);
      }

      List<MonsterDropEntry> effectiveLoot = new LinkedList<>();
      for (MonsterDropEntry mde : loots) {
         if (isRelevantDrop(mde, players, playersInv)) {
            effectiveLoot.add(mde);
         }
      }

      return effectiveLoot;
   }

}
