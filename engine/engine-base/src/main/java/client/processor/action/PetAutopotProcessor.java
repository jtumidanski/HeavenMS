package client.processor.action;

import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.database.AbstractQueryExecutor;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import config.YamlConfig;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;

public class PetAutopotProcessor extends AbstractQueryExecutor {
   private static PetAutopotProcessor instance;

   public static PetAutopotProcessor getInstance() {
      if (instance == null) {
         instance = new PetAutopotProcessor();
      }
      return instance;
   }

   private PetAutopotProcessor() {
   }

   private static class AutopotAction {

      private MapleClient c;
      private short slot;
      private int itemId;

      private Item toUse;
      private List<Item> toUseList;

      private boolean hasHpGain, hasMpGain;
      private int maxHp, maxMp, curHp, curMp;
      private double incHp, incMp;

      private boolean cursorOnNextAvailablePot(MapleCharacter chr) {
         if (toUseList == null) {
            toUseList = chr.getInventory(MapleInventoryType.USE).linkedListById(itemId);
         }

         toUse = null;
         while (!toUseList.isEmpty()) {
            Item it = toUseList.remove(0);

            if (it.quantity() > 0) {
               toUse = it;
               slot = it.position();

               return true;
            }
         }

         return false;
      }

      public AutopotAction(MapleClient c, short slot, int itemId) {
         this.c = c;
         this.slot = slot;
         this.itemId = itemId;
      }

      public void run() {
         MapleClient c = this.c;
         MapleCharacter chr = c.getPlayer();
         if (!chr.isAlive()) {
            PacketCreator.announce(c, new EnableActions());
            return;
         }

         int useCount = 0, qtyCount = 0;
         MapleStatEffect stat = null;

         MapleInventory useInv = chr.getInventory(MapleInventoryType.USE);
         useInv.lockInventory();
         try {
            toUse = useInv.getItem(slot);
            if (toUse != null) {
               if (toUse.id() != itemId) {
                  PacketCreator.announce(c, new EnableActions());
                  return;
               }

               toUseList = null;

               // from now on, toUse becomes the "cursor" for the current pot being used
               if (toUse.quantity() <= 0) {
                  if (!cursorOnNextAvailablePot(chr)) {
                     PacketCreator.announce(c, new EnableActions());
                     return;
                  }
               }

               stat = MapleItemInformationProvider.getInstance().getItemEffect(toUse.id());
               hasHpGain = stat.getHp() > 0 || stat.getHpRate() > 0.0;
               hasMpGain = stat.getMp() > 0 || stat.getMpRate() > 0.0;

               maxHp = chr.getCurrentMaxHp();
               maxMp = chr.getCurrentMaxMp();

               curHp = chr.getHp();
               curMp = chr.getMp();

               incHp = stat.getHp();
               if (incHp <= 0 && hasHpGain) {
                  incHp = Math.ceil(maxHp * stat.getHpRate());
               }

               incMp = stat.getMp();
               if (incMp <= 0 && hasMpGain) {
                  incMp = Math.ceil(maxMp * stat.getMpRate());
               }

               if (YamlConfig.config.server.USE_COMPULSORY_AUTOPOT) {
                  if (hasHpGain) {
                     qtyCount = (int) Math.ceil(((YamlConfig.config.server.PET_AUTOHP_RATIO * maxHp) - curHp) / incHp);
                  }

                  if (hasMpGain) {
                     qtyCount = Math.max(qtyCount, (int) Math.ceil(((YamlConfig.config.server.PET_AUTOMP_RATIO * maxMp) - curMp) / incMp));
                  }
               } else {
                  qtyCount = 1;   // non-compulsory autopot concept thanks to marcuswoon
               }

               while (true) {
                  short qtyToUse = (short) Math.min(qtyCount, toUse.quantity());
                  MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, qtyToUse, false);

                  curHp += (incHp * qtyToUse);
                  curMp += (incMp * qtyToUse);

                  useCount += qtyToUse;
                  qtyCount -= qtyToUse;

                  if (toUse.quantity() == 0 && qtyCount > 0) {
                     // depleted out the current slot, fetch for more

                     if (!cursorOnNextAvailablePot(chr)) {
                        break;    // no more pots available
                     }
                  } else {
                     break;    // gracefully finished it's job, quit the loop
                  }
               }
            }
         } finally {
            useInv.unlockInventory();
         }

         for (int i = 0; i < useCount; i++) {
            stat.applyTo(chr);
         }

         PacketCreator.announce(chr, new EnableActions());
      }
   }

   public void runAutopotAction(MapleClient c, short slot, int itemid) {
      AutopotAction action = new AutopotAction(c, slot, itemid);
      action.run();
   }
}