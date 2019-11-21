/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

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
package net.server.channel.handlers;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.processor.action.MakerProcessor;
import config.YamlConfig;
import constants.game.GameConstants;
import constants.inventory.ItemConstants;
import net.server.AbstractPacketHandler;
import net.server.PacketReader;
import net.server.channel.packet.maker.BaseMakerActionPacket;
import net.server.channel.packet.maker.MakerDisassemblingPacket;
import net.server.channel.packet.maker.MakerReagentPacket;
import net.server.channel.packet.reader.MakerActionReader;
import server.MakerItemFactory;
import server.MapleItemInformationProvider;
import tools.FilePrinter;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.ServerNoticeType;
import tools.packet.foreigneffect.ShowForeignMakerEffect;
import tools.packet.maker.MakerCrystalResult;
import tools.packet.maker.MakerEnableActions;
import tools.packet.maker.MakerResult;
import tools.packet.maker.MakerResultDesynth;
import tools.packet.showitemgaininchat.ShowMakerEffect;

/**
 * @author Jay Estrella, Ronan
 */
public final class MakerSkillHandler extends AbstractPacketHandler<BaseMakerActionPacket> {
   @Override
   public Class<? extends PacketReader<BaseMakerActionPacket>> getReaderClass() {
      return MakerActionReader.class;
   }

   @Override
   public void handlePacket(BaseMakerActionPacket packet, MapleClient client) {
      if (client.tryAcquireClient()) {
         try {
            int type = packet.theType();
            int toCreate = packet.toCreate();
            int toDisassemble = -1, pos = -1;
            boolean makerSucceeded = true;

            MakerItemFactory.MakerItemCreateEntry recipe;
            Map<Integer, Short> reagentids = new LinkedHashMap<>();
            int stimulantid = -1;

            if (type == 3) {    // building monster crystal
               int fromLeftover = toCreate;
               toCreate = MapleItemInformationProvider.getInstance().getMakerCrystalFromLeftover(toCreate);
               if (toCreate == -1) {
                  MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, MapleItemInformationProvider.getInstance().getName(fromLeftover) + " is unavailable for Monster Crystal conversion.");
                  PacketCreator.announce(client, new MakerEnableActions());
                  return;
               }

               recipe = MakerItemFactory.generateLeftoverCrystalEntry(fromLeftover, toCreate);
            } else if (type == 4 && packet instanceof MakerDisassemblingPacket) {  // disassembling
               pos = ((MakerDisassemblingPacket) packet).position();

               Item it = client.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short) pos);
               if (it != null && it.id() == toCreate) {
                  toDisassemble = toCreate;

                  Pair<Integer, List<Pair<Integer, Integer>>> p = generateDisassemblyInfo(toDisassemble);
                  if (p != null) {
                     recipe = MakerItemFactory.generateDisassemblyCrystalEntry(toDisassemble, p.getLeft(), p.getRight());
                  } else {
                     MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, MapleItemInformationProvider.getInstance().getName(toCreate) + " is unavailable for Monster Crystal disassembly.");
                     PacketCreator.announce(client, new MakerEnableActions());
                     return;
                  }
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, "An unknown error occurred when trying to apply that item for disassembly.");
                  PacketCreator.announce(client, new MakerEnableActions());
                  return;
               }
            } else {
               if (ItemConstants.isEquipment(toCreate) && packet instanceof MakerReagentPacket) {   // only equips uses stimulant and reagents
                  if (((MakerReagentPacket) packet).isStimulant()) {  // stimulant
                     stimulantid = MapleItemInformationProvider.getInstance().getMakerStimulant(toCreate);
                     if (!client.getAbstractPlayerInteraction().haveItem(stimulantid)) {
                        stimulantid = -1;
                     }
                  }

                  for (int i = 0; i < ((MakerReagentPacket) packet).reagentCount(); i++) {
                     int reagentid = ((MakerReagentPacket) packet).reagentIds()[i];
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
                     int qty = client.getAbstractPlayerInteraction().getItemQuantity(r.getKey());

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
                        MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, "You can only use WATK and MATK Strengthening Gems on weapon items.");
                        PacketCreator.announce(client, new MakerEnableActions());
                        return;
                     }
                  }
               }

               recipe = MakerItemFactory.getItemCreateEntry(toCreate, stimulantid, reagentids);
            }

            short createStatus = getCreateStatus(client, recipe);

            switch (createStatus) {
               case -1:// non-available for Maker itemid has been tried to forge
                  FilePrinter.printError(FilePrinter.EXPLOITS, "Player " + client.getPlayer().getName() + " tried to craft itemid " + toCreate + " using the Maker skill.");
                  MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, "The requested item could not be crafted on this operation.");
                  PacketCreator.announce(client, new MakerEnableActions());
                  break;

               case 1: // no items
                  MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, "You don't have all required items in your inventory to make " + MapleItemInformationProvider.getInstance().getName(toCreate) + ".");
                  PacketCreator.announce(client, new MakerEnableActions());
                  break;

               case 2: // no meso
                  MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, "You don't have enough mesos (" + GameConstants.numberWithCommas(recipe.getCost()) + ") to complete this operation.");
                  PacketCreator.announce(client, new MakerEnableActions());
                  break;

               case 3: // no req level
                  MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, "You don't have enough level to complete this operation.");
                  PacketCreator.announce(client, new MakerEnableActions());
                  break;

               case 4: // no req skill level
                  MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, "You don't have enough Maker level to complete this operation.");
                  PacketCreator.announce(client, new MakerEnableActions());
                  break;

               case 5: // inventory full
                  MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, "Your inventory is full.");
                  PacketCreator.announce(client, new MakerEnableActions());
                  break;

               default:
                  if (toDisassemble != -1) {
                     MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.EQUIP, (short) pos, (short) 1, false);
                  } else {
                     for (Pair<Integer, Integer> p : recipe.getReqItems()) {
                        client.getAbstractPlayerInteraction().gainItem(p.getLeft(), (short) -p.getRight(), false);
                     }
                  }

                  int cost = recipe.getCost();
                  if (stimulantid == -1 && reagentids.isEmpty()) {
                     if (cost > 0) {
                        client.getPlayer().gainMeso(-cost, false);
                     }

                     for (Pair<Integer, Integer> p : recipe.getGainItems()) {
                        client.getPlayer().setCS(true);
                        client.getAbstractPlayerInteraction().gainItem(p.getLeft(), p.getRight().shortValue(), false);
                        client.getPlayer().setCS(false);
                     }
                  } else {
                     toCreate = recipe.getGainItems().get(0).getLeft();

                     if (stimulantid != -1) {
                        client.getAbstractPlayerInteraction().gainItem(stimulantid, (short) -1, false);
                     }
                     if (!reagentids.isEmpty()) {
                        for (Map.Entry<Integer, Short> r : reagentids.entrySet()) {
                           client.getAbstractPlayerInteraction().gainItem(r.getKey(), (short) (-1 * r.getValue()), false);
                        }
                     }

                     if (cost > 0) {
                        client.getPlayer().gainMeso(-cost, false);
                     }
                     makerSucceeded = addBoostedMakerItem(client, toCreate, stimulantid, reagentids);
                  }

                  // thanks inhyuk for noticing missing MAKER_RESULT packets
                  if (type == 3) {
                     PacketCreator.announce(client, new MakerCrystalResult(recipe.getGainItems().get(0).getLeft(), recipe.getReqItems().get(0).getLeft()));
                  } else if (type == 4) {
                     PacketCreator.announce(client, new MakerResultDesynth(recipe.getReqItems().get(0).getLeft(), recipe.getCost(), recipe.getGainItems()));
                  } else {
                     PacketCreator.announce(client, new MakerResult(makerSucceeded, recipe.getGainItems().get(0).getLeft(), recipe.getGainItems().get(0).getRight(), recipe.getCost(), recipe.getReqItems(), stimulantid, new LinkedList<>(reagentids.keySet())));
                  }

                  PacketCreator.announce(client, new ShowMakerEffect(makerSucceeded));
                  boolean finalMakerSucceeded = makerSucceeded;
                  MasterBroadcaster.getInstance().sendToAllInMap(client.getPlayer().getMap(),
                        new ShowForeignMakerEffect(client.getPlayer().getId(), finalMakerSucceeded), false, client.getPlayer());

                  if (toCreate == 4260003 && type == 3 && client.getPlayer().getQuestStatus(6033) == 1) {
                     client.getAbstractPlayerInteraction().setQuestProgress(6033, 1);
                  }
            }
         } finally {
            client.releaseClient();
         }
      }
   }

   private Pair<Integer, List<Pair<Integer, Integer>>> generateDisassemblyInfo(int itemId) {
      int recvFee = MapleItemInformationProvider.getInstance().getMakerDisassembledFee(itemId);
      if (recvFee > -1) {
         List<Pair<Integer, Integer>> gains = MapleItemInformationProvider.getInstance().getMakerDisassembledItems(itemId);
         if (!gains.isEmpty()) {
            return new Pair<>(recvFee, gains);
         }
      }

      return null;
   }

   // checks and prevents hackers from PE'ing Maker operations with invalid operations
   private boolean removeOddMakerReagents(int toCreate, Map<Integer, Short> reagentids) {
      Map<Integer, Integer> reagentType = new LinkedHashMap<>();
      List<Integer> toRemove = new LinkedList<>();

      boolean isWeapon = ItemConstants.isWeapon(toCreate) || YamlConfig.config.server.USE_MAKER_PERMISSIVE_ATKUP;  // thanks Vcoc for finding a case where a weapon wouldn't be counted as such due to a bounding on isWeapon

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

   private short getCreateStatus(MapleClient c, MakerItemFactory.MakerItemCreateEntry recipe) {
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

      if (MakerProcessor.getInstance().getMakerSkillLevel(c.getPlayer()) < recipe.getReqSkillLevel()) {
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

   private boolean hasItems(MapleClient c, MakerItemFactory.MakerItemCreateEntry recipe) {
      for (Pair<Integer, Integer> p : recipe.getReqItems()) {
         int itemId = p.getLeft();
         if (c.getPlayer().getInventory(ItemConstants.getInventoryType(itemId)).countById(itemId) < p.getRight()) {
            return false;
         }
      }
      return true;
   }

   private boolean addBoostedMakerItem(MapleClient c, int itemid, int stimulantid, Map<Integer, Short> reagentids) {
      if (stimulantid != -1 && !MapleItemInformationProvider.rollSuccessChance(90.0)) {
         return false;
      }

      Item item = MapleItemInformationProvider.getInstance().getEquipById(itemid);
      if (item == null) {
         return false;
      }

      Equip eqp = (Equip) item;
      if (ItemConstants.isAccessory(item.id()) && eqp.slots() <= 0) {
         eqp.slots_$eq(3);
      }

      if (YamlConfig.config.server.USE_ENHANCED_CRAFTING) {
         if (!(c.getPlayer().isGM() && YamlConfig.config.server.USE_PERFECT_GM_SCROLL)) {
            eqp.slots_$eq((byte) (eqp.slots() + 1));
         }
         item = MapleItemInformationProvider.getInstance().scrollEquipWithId(eqp, 2049100, true, 2049100, c.getPlayer().isGM());
      }

      if (!reagentids.isEmpty()) {
         Map<String, Integer> stats = new LinkedHashMap<>();
         List<Short> randOption = new LinkedList<>();
         List<Short> randStat = new LinkedList<>();

         for (Map.Entry<Integer, Short> r : reagentids.entrySet()) {
            Pair<String, Integer> reagentBuff = MapleItemInformationProvider.getInstance().getMakerReagentStatUpgrade(r.getKey());

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
            MapleItemInformationProvider.getInstance().scrollOptionEquipWithChaos(eqp, sh, false);
         }

         for (Short sh : randOption) {
            MapleItemInformationProvider.getInstance().scrollOptionEquipWithChaos(eqp, sh, true);
         }
      }

      if (stimulantid != -1) {
         eqp = MapleItemInformationProvider.getInstance().randomizeUpgradeStats(eqp);
      }

      MapleInventoryManipulator.addFromDrop(c, item, false, -1);
      return true;
   }
}
