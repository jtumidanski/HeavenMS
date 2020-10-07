package server.quest.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import constants.inventory.ItemConstants;
import provider.MapleData;
import provider.MapleDataTool;
import server.MapleItemInformationProvider;
import server.quest.MapleQuestActionType;
import tools.I18nMessage;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.Randomizer;
import tools.ServerNoticeType;
import tools.packet.showitemgaininchat.ShowItemGainInChat;

public class ItemAction extends MapleQuestAction {
   private List<ItemData> items = new ArrayList<>();

   public ItemAction(int questId, MapleData data) {
      super(questId, MapleQuestActionType.ITEM);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      for (MapleData iEntry : data.getChildren()) {
         int id = MapleDataTool.getInt(iEntry.getChildByPath("id"));
         int count = MapleDataTool.getInt(iEntry.getChildByPath("count"), 1);
         int period = MapleDataTool.getInt(iEntry.getChildByPath("period"), 0);

         int prop = -1;
         MapleData propData = iEntry.getChildByPath("prop");
         if (propData != null) {
            prop = MapleDataTool.getInt(propData);
         }

         int gender = 2;
         if (iEntry.getChildByPath("gender") != null) {
            gender = MapleDataTool.getInt(iEntry.getChildByPath("gender"));
         }

         int job = -1;
         if (iEntry.getChildByPath("job") != null) {
            job = MapleDataTool.getInt(iEntry.getChildByPath("job"));
         }

         items.add(new ItemData(Integer.parseInt(iEntry.getName()), id, count, prop, job, gender, period));
      }

      items.sort(Comparator.comparingInt(ItemData::map));
   }

   @Override
   public void run(MapleCharacter chr, Integer extSelection) {
      List<ItemData> takeItem = new LinkedList<>();
      List<ItemData> giveItem = new LinkedList<>();

      int props = 0, rndProps = 0, accProps = 0;
      for (ItemData item : items) {
         if (item.prop() != -1 && item.prop() != -1 && canGetItem(item, chr)) {
            props += item.prop();
         }
      }

      int extNum = 0;
      if (props > 0) {
         rndProps = Randomizer.nextInt(props);
      }
      for (ItemData iEntry : items) {
         if (!canGetItem(iEntry, chr)) {
            continue;
         }
         if (iEntry.prop() != -1) {
            if (iEntry.prop() == -1) {
               if (extSelection != extNum++) {
                  continue;
               }
            } else {
               accProps += iEntry.prop();

               if (accProps <= rndProps) {
                  continue;
               } else {
                  accProps = Integer.MIN_VALUE;
               }
            }
         }

         if (iEntry.count() < 0) { // Remove Item
            takeItem.add(iEntry);
         } else {                    // Give Item
            giveItem.add(iEntry);
         }
      }

      // must take all needed items before giving others

      for (ItemData iEntry : takeItem) {
         int itemId = iEntry.id(), count = iEntry.count();

         MapleInventoryType type = ItemConstants.getInventoryType(itemId);
         int quantity = count * -1; // Invert
         if (type.equals(MapleInventoryType.EQUIP)) {
            if (chr.getInventory(type).countById(itemId) < quantity) {
               // Not enough in the equip inventory, so check Equipped...
               if (chr.getInventory(MapleInventoryType.EQUIPPED).countById(itemId) > quantity) {
                  // Found it equipped, so change the type to equipped.
                  type = MapleInventoryType.EQUIPPED;
               }
            }
         }

         MapleInventoryManipulator.removeById(chr.getClient(), type, itemId, quantity, true, false);
         PacketCreator.announce(chr, new ShowItemGainInChat(itemId, (short) count));
      }

      for (ItemData iEntry : giveItem) {
         int itemId = iEntry.id(), count = iEntry.count(), period = iEntry.period();
         MapleInventoryManipulator.addById(chr.getClient(), itemId, (short) count, "", -1,
               period > 0 ? (System.currentTimeMillis() + period * 60 * 1000) : -1);
         PacketCreator.announce(chr, new ShowItemGainInChat(itemId, (short) count));
      }
   }

   @Override
   public boolean check(MapleCharacter chr, Integer extSelection) {
      List<Pair<Item, MapleInventoryType>> gainList = new LinkedList<>();
      List<Pair<Item, MapleInventoryType>> selectList = new LinkedList<>();
      List<Pair<Item, MapleInventoryType>> randomList = new LinkedList<>();

      List<Integer> allSlotUsed = new ArrayList<>(5);
      for (byte i = 0; i < 5; i++) {
         allSlotUsed.add(0);
      }

      for (ItemData item : items) {
         if (!canGetItem(item, chr)) {
            continue;
         }

         MapleInventoryType type = ItemConstants.getInventoryType(item.id());
         if (item.prop() != -1) {
            Item toItem = new Item(item.id(), (short) 0, (short) item.count());

            if (item.prop() < 0) {
               selectList.add(new Pair<>(toItem, type));
            } else {
               randomList.add(new Pair<>(toItem, type));
            }
         } else {
            // Make sure they can hold the item.
            Item toItem = new Item(item.id(), (short) 0, (short) item.count());
            gainList.add(new Pair<>(toItem, type));

            if (item.count() < 0) {
               // Make sure they actually have the item.
               int quantity = item.count() * -1;

               int freeSlotCount = chr.getInventory(type).freeSlotCountById(item.id(), quantity);
               if (freeSlotCount == -1) {
                  if (type.equals(MapleInventoryType.EQUIP)
                        && chr.getInventory(MapleInventoryType.EQUIPPED).countById(item.id()) > quantity) {
                     continue;
                  }

                  announceInventoryLimit(Collections.singletonList(item.id()), chr);
                  return false;
               } else {
                  int idx = type.getType() - 1;   // more slots available from the given items!
                  allSlotUsed.set(idx, allSlotUsed.get(idx) - freeSlotCount);
               }
            }
         }
      }

      if (!randomList.isEmpty()) {
         int result;
         MapleClient c = chr.getClient();

         List<Integer> rndUsed = new ArrayList<>(5);
         for (byte i = 0; i < 5; i++) {
            rndUsed.add(allSlotUsed.get(i));
         }

         for (Pair<Item, MapleInventoryType> it : randomList) {
            int idx = it.getRight().getType() - 1;

            result = MapleInventoryManipulator
                  .checkSpaceProgressively(c, it.getLeft().id(), it.getLeft().quantity(), "", rndUsed.get(idx), false);
            if (result % 2 == 0) {
               announceInventoryLimit(Collections.singletonList(it.getLeft().id()), chr);
               return false;
            }

            allSlotUsed.set(idx, Math.max(allSlotUsed.get(idx), result >> 1));
         }
      }

      if (!selectList.isEmpty()) {
         Pair<Item, MapleInventoryType> selected = selectList.get(extSelection);
         gainList.add(selected);
      }

      if (!canHold(chr, gainList)) {
         List<Integer> gainItemIds = new LinkedList<>();
         for (Pair<Item, MapleInventoryType> it : gainList) {
            gainItemIds.add(it.getLeft().id());
         }

         announceInventoryLimit(gainItemIds, chr);
         return false;
      }
      return true;
   }

   private void announceInventoryLimit(List<Integer> itemIds, MapleCharacter chr) {
      for (Integer id : itemIds) {
         if (MapleItemInformationProvider.getInstance().isPickupRestricted(id) && chr.haveItemWithId(id, true)) {
            MessageBroadcaster.getInstance()
                  .sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("INVENTORY_ONE_OF_A_KIND_LIMIT"));
            return;
         }
      }

      MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("INVENTORY_LIMIT"));
   }

   private boolean canHold(MapleCharacter chr, List<Pair<Item, MapleInventoryType>> gainList) {
      List<Integer> toAddItemIds = new LinkedList<>();
      List<Integer> toAddQuantity = new LinkedList<>();
      List<Integer> toRemoveItemIds = new LinkedList<>();
      List<Integer> toRemoveQuantity = new LinkedList<>();

      for (Pair<Item, MapleInventoryType> item : gainList) {
         Item it = item.getLeft();

         if (it.quantity() > 0) {
            toAddItemIds.add(it.id());
            toAddQuantity.add((int) it.quantity());
         } else {
            toRemoveItemIds.add(it.id());
            toRemoveQuantity.add(-1 * ((int) it.quantity()));
         }
      }

      return chr.getAbstractPlayerInteraction()
            .canHoldAllAfterRemoving(toAddItemIds, toAddQuantity, toRemoveItemIds, toRemoveQuantity);
   }

   private boolean canGetItem(ItemData item, MapleCharacter chr) {
      if (item.gender() != 2 && item.gender() != chr.getGender()) {
         return false;
      }

      if (item.job() > 0) {
         final List<Integer> code = getJobBy5ByteEncoding(item.job());
         boolean jobFound = false;
         for (int codec : code) {
            if (codec / 100 == chr.getJob().getId() / 100) {
               jobFound = true;
               break;
            }
         }
         return jobFound;
      }

      return true;
   }

   public boolean restoreLostItem(MapleCharacter chr, int itemId) {
      if (!MapleItemInformationProvider.getInstance().isQuestItem(itemId)) {
         return false;
      }

      for (ItemData item : items) {
         if (item.id() == itemId) {
            int missingQty = item.count() - chr.countItem(itemId);
            if (missingQty > 0) {
               if (!chr.canHold(itemId, missingQty)) {
                  MessageBroadcaster.getInstance()
                        .sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("INVENTORY_LIMIT"));
                  return false;
               }

               MapleInventoryManipulator.addById(chr.getClient(), item.id(), (short) missingQty);
               LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.QUEST_RESTORE_ITEM,
                     chr + " obtained " + itemId + " qty. " + missingQty + " from quest " + questId);
            }
            return true;
         }
      }

      return false;
   }
} 
