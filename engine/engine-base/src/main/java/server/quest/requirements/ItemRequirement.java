package server.quest.requirements;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import client.MapleCharacter;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.inventory.ItemConstants;
import provider.MapleData;
import provider.MapleDataTool;
import server.MapleItemInformationProvider;
import server.quest.MapleQuestRequirementType;
import tools.I18nMessage;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class ItemRequirement extends MapleQuestRequirement {
   Map<Integer, Integer> items = new HashMap<>();

   public ItemRequirement(int questId, MapleData data) {
      super(questId, MapleQuestRequirementType.ITEM);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      for (MapleData itemEntry : data.getChildren()) {
         int itemId = MapleDataTool.getInt(itemEntry.getChildByPath("id"));
         int count = MapleDataTool.getInt(itemEntry.getChildByPath("count"), 0);

         items.put(itemId, count);
      }
   }

   @Override
   public boolean check(MapleCharacter chr, Integer npcId) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      for (Integer itemId : items.keySet()) {
         int countNeeded = items.get(itemId);
         int count = 0;

         MapleInventoryType iType = ItemConstants.getInventoryType(itemId);

         if (iType.equals(MapleInventoryType.UNDEFINED)) {
            return false;
         }
         for (Item item : chr.getInventory(iType).listById(itemId)) {
            count += item.quantity();
         }
         //Weird stuff, nexon made some quests only available when wearing gm clothes. This enables us to accept it ><
         if (iType.equals(MapleInventoryType.EQUIP) && !ItemConstants.isMedal(itemId)) {
            if (chr.isGM()) {
               for (Item item : chr.getInventory(MapleInventoryType.EQUIPPED).listById(itemId)) {
                  count += item.quantity();
               }
            } else {
               if (count < countNeeded) {
                  if (chr.getInventory(MapleInventoryType.EQUIPPED).countById(itemId) + count >= countNeeded) {
                     MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT,
                           I18nMessage.from("QUEST_ITEM_UNEQUIP_REQUIREMENT").with(ii.getName(itemId)));
                     return false;
                  }
               }
            }
         }

         if (count < countNeeded || countNeeded <= 0 && count > 0) {
            return false;
         }
      }
      return true;
   }

   public int getItemAmountNeeded(int itemId, boolean complete) {
      Integer amount = items.get(itemId);
      return Objects.requireNonNullElse(amount, complete ? Integer.MAX_VALUE : Integer.MIN_VALUE);
   }
}
