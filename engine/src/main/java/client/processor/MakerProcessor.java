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
package client.processor;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import constants.GameConstants;
import constants.ItemConstants;
import constants.ServerConstants;
import server.MakerItemFactory;
import server.MakerItemFactory.MakerItemCreateEntry;
import server.MapleItemInformationProvider;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.Pair;
import tools.ServerNoticeType;
import tools.data.input.SeekableLittleEndianAccessor;

/**
 * @author Ronan
 */
public class MakerProcessor {

   private static MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

   public static void makerAction(SeekableLittleEndianAccessor slea, MapleClient c) {
      if (c.tryAcquireClient()) {
         try {
            int type = slea.readInt();
            int toCreate = slea.readInt();
            int toDisassemble = -1, pos = -1;
            boolean makerSucceeded = true;

            MakerItemCreateEntry recipe;
            Map<Integer, Short> reagentids = new LinkedHashMap<>();
            int stimulantid = -1;

            if (type == 3) {    // building monster crystal
               int fromLeftover = toCreate;
               toCreate = ii.getMakerCrystalFromLeftover(toCreate);
               if (toCreate == -1) {
                  MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, ii.getName(fromLeftover) + " is unavailable for Monster Crystal conversion.");
                  c.announce(MaplePacketCreator.makerEnableActions());
                  return;
               }

               recipe = MakerItemFactory.generateLeftoverCrystalEntry(fromLeftover, toCreate);
            } else if (type == 4) {  // disassembling
               slea.readInt(); // 1... probably inventory type
               pos = slea.readInt();

               Item it = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short) pos);
               if (it != null && it.getItemId() == toCreate) {
                  toDisassemble = toCreate;

                  Pair<Integer, List<Pair<Integer, Integer>>> p = generateDisassemblyInfo(toDisassemble);
                  if (p != null) {
                     recipe = MakerItemFactory.generateDisassemblyCrystalEntry(toDisassemble, p.getLeft(), p.getRight());
                  } else {
                     MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, ii.getName(toCreate) + " is unavailable for Monster Crystal disassembly.");
                     c.announce(MaplePacketCreator.makerEnableActions());
                     return;
                  }
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "An unknown error occurred when trying to apply that item for disassembly.");
                  c.announce(MaplePacketCreator.makerEnableActions());
                  return;
               }
            } else {
               if (ItemConstants.isEquipment(toCreate)) {   // only equips uses stimulant and reagents
                  if (slea.readByte() != 0) {  // stimulant
                     stimulantid = ii.getMakerStimulant(toCreate);
                     if (!c.getAbstractPlayerInteraction().haveItem(stimulantid)) {
                        stimulantid = -1;
                     }
                  }

                  int reagents = Math.min(slea.readInt(), getMakerReagentSlots(toCreate));
                  for (int i = 0; i < reagents; i++) {  // crystals
                     int reagentid = slea.readInt();
                     if (ItemConstants.isMakerReagent(reagentid)) {
                        Short rs = reagentids.get(reagentid);
                        if (rs == null) {
                           reagentids.put(reagentid, (short) 1);
                        } else {
                           reagentids.put(reagentid, (short) (rs + 1));
                        }
                     }
                  }

                  List<Pair<Integer, Short>> toUpdate = new LinkedList<>();
                  for (Map.Entry<Integer, Short> r : reagentids.entrySet()) {
                     int qty = c.getAbstractPlayerInteraction().getItemQuantity(r.getKey());

                     if (qty < r.getValue()) {
                        toUpdate.add(new Pair<>(r.getKey(), (short) qty));
                     }
                  }

                  // remove those not present on player inventory
                  if (!toUpdate.isEmpty()) {
                     for (Pair<Integer, Short> rp : toUpdate) {
                        if (rp.getRight() > 0) {
                           reagentids.put(rp.getLeft(), rp.getRight());
                        } else {
                           reagentids.remove(rp.getLeft());
                        }
                     }
                  }

                  if (!reagentids.isEmpty()) {
                     if (!removeOddMakerReagents(toCreate, reagentids)) {
                        MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "You can only use WATK and MATK Strengthening Gems on weapon items.");
                        c.announce(MaplePacketCreator.makerEnableActions());
                        return;
                     }
                  }
               }

               recipe = MakerItemFactory.getItemCreateEntry(toCreate, stimulantid, reagentids);
            }

            short createStatus = getCreateStatus(c, recipe);

            switch (createStatus) {
               case -1:// non-available for Maker itemid has been tried to forge
                  FilePrinter.printError(FilePrinter.EXPLOITS, "Player " + c.getPlayer().getName() + " tried to craft itemid " + toCreate + " using the Maker skill.");
                  MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "The requested item could not be crafted on this operation.");
                  c.announce(MaplePacketCreator.makerEnableActions());
                  break;

               case 1: // no items
                  MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "You don't have all required items in your inventory to make " + ii.getName(toCreate) + ".");
                  c.announce(MaplePacketCreator.makerEnableActions());
                  break;

               case 2: // no meso
                  MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "You don't have enough mesos (" + GameConstants.numberWithCommas(recipe.getCost()) + ") to complete this operation.");
                  c.announce(MaplePacketCreator.makerEnableActions());
                  break;

               case 3: // no req level
                  MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "You don't have enough level to complete this operation.");
                  c.announce(MaplePacketCreator.makerEnableActions());
                  break;

               case 4: // no req skill level
                  MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "You don't have enough Maker level to complete this operation.");
                  c.announce(MaplePacketCreator.makerEnableActions());
                  break;

               case 5: // inventory full
                  MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "Your inventory is full.");
                  c.announce(MaplePacketCreator.makerEnableActions());
                  break;

               default:
                  if (toDisassemble != -1) {
                     MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, (short) pos, (short) 1, false);
                  } else {
                     for (Pair<Integer, Integer> p : recipe.getReqItems()) {
                        c.getAbstractPlayerInteraction().gainItem(p.getLeft(), (short) -p.getRight(), false);
                     }
                  }

                  int cost = recipe.getCost();
                  if (stimulantid == -1 && reagentids.isEmpty()) {
                     if (cost > 0) {
                        c.getPlayer().gainMeso(-cost, false);
                     }

                     for (Pair<Integer, Integer> p : recipe.getGainItems()) {
                        c.getPlayer().setCS(true);
                        c.getAbstractPlayerInteraction().gainItem(p.getLeft(), p.getRight().shortValue(), false);
                        c.getPlayer().setCS(false);
                     }
                  } else {
                     toCreate = recipe.getGainItems().get(0).getLeft();

                     if (stimulantid != -1) {
                        c.getAbstractPlayerInteraction().gainItem(stimulantid, (short) -1, false);
                     }
                     if (!reagentids.isEmpty()) {
                        for (Map.Entry<Integer, Short> r : reagentids.entrySet()) {
                           c.getAbstractPlayerInteraction().gainItem(r.getKey(), (short) (-1 * r.getValue()), false);
                        }
                     }

                     if (cost > 0) {
                        c.getPlayer().gainMeso(-cost, false);
                     }
                     makerSucceeded = addBoostedMakerItem(c, toCreate, stimulantid, reagentids);
                  }

                  // thanks inhyuk for noticing missing MAKER_RESULT packets
                  if (type == 3) {
                     c.announce(MaplePacketCreator.makerResultCrystal(recipe.getGainItems().get(0).getLeft(), recipe.getReqItems().get(0).getLeft()));
                  } else if (type == 4) {
                     c.announce(MaplePacketCreator.makerResultDesynth(recipe.getReqItems().get(0).getLeft(), recipe.getCost(), recipe.getGainItems()));
                  } else {
                     c.announce(MaplePacketCreator.makerResult(makerSucceeded, recipe.getGainItems().get(0).getLeft(), recipe.getGainItems().get(0).getRight(), recipe.getCost(), recipe.getReqItems(), stimulantid, new LinkedList<>(reagentids.keySet())));
                  }

                  c.announce(MaplePacketCreator.showMakerEffect(makerSucceeded));
                  c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.showForeignMakerEffect(c.getPlayer().getId(), makerSucceeded), false);

                  if (toCreate == 4260003 && type == 3 && c.getPlayer().getQuestStatus(6033) == 1) {
                     c.getAbstractPlayerInteraction().setQuestProgress(6033, 1);
                  }
            }
         } finally {
            c.releaseClient();
         }
      }
   }

   // checks and prevents hackers from PE'ing Maker operations with invalid operations
   private static boolean removeOddMakerReagents(int toCreate, Map<Integer, Short> reagentids) {
      Map<Integer, Integer> reagentType = new LinkedHashMap<>();
      List<Integer> toRemove = new LinkedList<>();

      boolean isWeapon = ItemConstants.isWeapon(toCreate) || ServerConstants.USE_MAKER_PERMISSIVE_ATKUP;  // thanks Vcoc for finding a case where a weapon wouldn't be counted as such due to a bounding on isWeapon

      for (Map.Entry<Integer, Short> r : reagentids.entrySet()) {
         int curRid = r.getKey();
         int type = r.getKey() / 100;

         if (type < 42502 && !isWeapon) {     // only weapons should gain w.att/m.att from these.
            return false;   //toRemove.add(curRid);
         } else {
            Integer tableRid = reagentType.get(type);

            if (tableRid != null) {
               if (tableRid < curRid) {
                  toRemove.add(tableRid);
                  reagentType.put(type, curRid);
               } else {
                  toRemove.add(curRid);
               }
            } else {
               reagentType.put(type, curRid);
            }
         }
      }

      // removing less effective gems of repeated type
      for (Integer i : toRemove) {
         reagentids.remove(i);
      }

      // only quantity 1 of each gem will be accepted by the Maker skill
      for (Integer i : reagentids.keySet()) {
         reagentids.put(i, (short) 1);
      }

      return true;
   }

   private static int getMakerReagentSlots(int itemId) {
      try {
         int eqpLevel = ii.getEquipLevelReq(itemId);

         if (eqpLevel < 78) {
            return 1;
         } else if (eqpLevel >= 78 && eqpLevel < 108) {
            return 2;
         } else {
            return 3;
         }
      } catch (NullPointerException npe) {
         return 0;
      }
   }

   private static Pair<Integer, List<Pair<Integer, Integer>>> generateDisassemblyInfo(int itemId) {
      int recvFee = ii.getMakerDisassembledFee(itemId);
      if (recvFee > -1) {
         List<Pair<Integer, Integer>> gains = ii.getMakerDisassembledItems(itemId);
         if (!gains.isEmpty()) {
            return new Pair<>(recvFee, gains);
         }
      }

      return null;
   }

   public static int getMakerSkillLevel(MapleCharacter chr) {
      return chr.getSkillLevel((chr.getJob().getId() / 1000) * 10000000 + 1007);
   }

   private static short getCreateStatus(MapleClient c, MakerItemCreateEntry recipe) {
      if (recipe == null) {
         return -1;
      }

      if (!hasItems(c, recipe)) {
         return 1;
      }

      if (c.getPlayer().getMeso() < recipe.getCost()) {
         return 2;
      }

      if (c.getPlayer().getLevel() < recipe.getReqLevel()) {
         return 3;
      }

      if (getMakerSkillLevel(c.getPlayer()) < recipe.getReqSkillLevel()) {
         return 4;
      }

      List<Integer> addItemids = new LinkedList<>();
      List<Integer> addQuantity = new LinkedList<>();
      List<Integer> rmvItemids = new LinkedList<>();
      List<Integer> rmvQuantity = new LinkedList<>();

      for (Pair<Integer, Integer> p : recipe.getReqItems()) {
         rmvItemids.add(p.getLeft());
         rmvQuantity.add(p.getRight());
      }

      for (Pair<Integer, Integer> p : recipe.getGainItems()) {
         addItemids.add(p.getLeft());
         addQuantity.add(p.getRight());
      }

      if (!c.getAbstractPlayerInteraction().canHoldAllAfterRemoving(addItemids, addQuantity, rmvItemids, rmvQuantity)) {
         return 5;
      }

      return 0;
   }

   private static boolean hasItems(MapleClient c, MakerItemCreateEntry recipe) {
      for (Pair<Integer, Integer> p : recipe.getReqItems()) {
         int itemId = p.getLeft();
         if (c.getPlayer().getInventory(ItemConstants.getInventoryType(itemId)).countById(itemId) < p.getRight()) {
            return false;
         }
      }
      return true;
   }

   private static boolean addBoostedMakerItem(MapleClient c, int itemid, int stimulantid, Map<Integer, Short> reagentids) {
      if (stimulantid != -1 && !MapleItemInformationProvider.rollSuccessChance(90.0)) {
         return false;
      }

      Item item = ii.getEquipById(itemid);
      if (item == null) {
         return false;
      }

      Equip eqp = (Equip) item;
      if (ItemConstants.isAccessory(item.getItemId()) && eqp.getUpgradeSlots() <= 0) {
         eqp.setUpgradeSlots(3);
      }

      if (ServerConstants.USE_ENHANCED_CRAFTING) {
         if (!(c.getPlayer().isGM() && ServerConstants.USE_PERFECT_GM_SCROLL)) {
            eqp.setUpgradeSlots((byte) (eqp.getUpgradeSlots() + 1));
         }
         item = MapleItemInformationProvider.getInstance().scrollEquipWithId(eqp, 2049100, true, 2049100, c.getPlayer().isGM());
      }

      if (!reagentids.isEmpty()) {
         Map<String, Integer> stats = new LinkedHashMap<>();
         List<Short> randOption = new LinkedList<>();
         List<Short> randStat = new LinkedList<>();

         for (Map.Entry<Integer, Short> r : reagentids.entrySet()) {
            Pair<String, Integer> reagentBuff = ii.getMakerReagentStatUpgrade(r.getKey());

            if (reagentBuff != null) {
               String s = reagentBuff.getLeft();

               if (s.substring(0, 4).contains("rand")) {
                  if (s.substring(4).equals("Stat")) {
                     randStat.add((short) (reagentBuff.getRight() * r.getValue()));
                  } else {
                     randOption.add((short) (reagentBuff.getRight() * r.getValue()));
                  }
               } else {
                  String stat = s.substring(3);

                  if (!stat.equals("ReqLevel")) {    // improve req level... really?
                     switch (stat) {
                        case "MaxHP":
                           stat = "MHP";
                           break;

                        case "MaxMP":
                           stat = "MMP";
                           break;
                     }
                     stats.merge(stat, reagentBuff.getRight() * r.getValue(), Integer::sum);
                  }
               }
            }
         }

         MapleItemInformationProvider.improveEquipStats(eqp, stats);

         for (Short sh : randStat) {
            ii.scrollOptionEquipWithChaos(eqp, sh, false);
         }

         for (Short sh : randOption) {
            ii.scrollOptionEquipWithChaos(eqp, sh, true);
         }
      }

      if (stimulantid != -1) {
         eqp = ii.randomizeUpgradeStats(eqp);
      }

      MapleInventoryManipulator.addFromDrop(c, item, false, -1);
      return true;
   }
}