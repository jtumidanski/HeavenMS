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
import tools.I18nMessage;
import tools.packet.foreigneffect.ShowForeignMakerEffect;
import tools.packet.maker.MakerCrystalResult;
import tools.packet.maker.MakerEnableActions;
import tools.packet.maker.MakerResult;
import tools.packet.maker.MakerResultDestroy;
import tools.packet.showitemgaininchat.ShowMakerEffect;

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
            Map<Integer, Short> reagentIds = new LinkedHashMap<>();
            int stimulantId = -1;

            if (type == 3) {    // building monster crystal
               int fromLeftover = toCreate;
               toCreate = MapleItemInformationProvider.getInstance().getMakerCrystalFromLeftover(toCreate);
               if (toCreate == -1) {
                  String itemName = MapleItemInformationProvider.getInstance().getName(fromLeftover);
                  MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("MAKER_SKILL_CRYSTAL_CONVERSION_ERROR").with(itemName));
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
                     String itemName = MapleItemInformationProvider.getInstance().getName(toCreate);
                     MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("MAKER_SKILL_CRYSTAL_DISASSEMBLY_ERROR").with(itemName));
                     PacketCreator.announce(client, new MakerEnableActions());
                     return;
                  }
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("MAKER_SKILL_UNKNOWN_DISASSEMBLY_ERROR"));
                  PacketCreator.announce(client, new MakerEnableActions());
                  return;
               }
            } else {
               if (ItemConstants.isEquipment(toCreate) && packet instanceof MakerReagentPacket) {   // only equips uses stimulant and reagents
                  if (((MakerReagentPacket) packet).isStimulant()) {  // stimulant
                     stimulantId = MapleItemInformationProvider.getInstance().getMakerStimulant(toCreate);
                     if (!client.getAbstractPlayerInteraction().haveItem(stimulantId)) {
                        stimulantId = -1;
                     }
                  }

                  for (int i = 0; i < ((MakerReagentPacket) packet).reagentCount(); i++) {
                     int reagentId = ((MakerReagentPacket) packet).reagentIds()[i];
                     if (ItemConstants.isMakerReagent(reagentId)) {
                        Short rs = reagentIds.get(reagentId);
                        if (rs == null) {
                           reagentIds.put(reagentId, (short) 1);
                        } else {
                           reagentIds.put(reagentId, (short) (rs + 1));
                        }
                     }
                  }

                  List<Pair<Integer, Short>> toUpdate = new LinkedList<>();
                  for (Map.Entry<Integer, Short> r : reagentIds.entrySet()) {
                     int qty = client.getAbstractPlayerInteraction().getItemQuantity(r.getKey());

                     if (qty < r.getValue()) {
                        toUpdate.add(new Pair<>(r.getKey(), (short) qty));
                     }
                  }

                  // remove those not present on player inventory
                  if (!toUpdate.isEmpty()) {
                     for (Pair<Integer, Short> rp : toUpdate) {
                        if (rp.getRight() > 0) {
                           reagentIds.put(rp.getLeft(), rp.getRight());
                        } else {
                           reagentIds.remove(rp.getLeft());
                        }
                     }
                  }

                  if (!reagentIds.isEmpty()) {
                     if (!removeOddMakerReagents(toCreate, reagentIds)) {
                        MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("MAKER_SKILL_WEAPON_ITEMS_CAN_ONLY_STRENGTHEN_BY_ATTACK"));
                        PacketCreator.announce(client, new MakerEnableActions());
                        return;
                     }
                  }
               }

               recipe = MakerItemFactory.getItemCreateEntry(toCreate, stimulantId, reagentIds);
            }

            short createStatus = getCreateStatus(client, recipe);

            switch (createStatus) {
               case -1:// non-available for Maker item id has been tried to forge
                  FilePrinter.printError(FilePrinter.EXPLOITS, "Player " + client.getPlayer().getName() + " tried to craft item id " + toCreate + " using the Maker skill.");
                  MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("MAKER_SKILL_REQUESTED_ITEM_COULD_NOT_BE_CRAFTED"));
                  PacketCreator.announce(client, new MakerEnableActions());
                  break;
               case 1: // no items
                  MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("MAKER_SKILL_MISSING_REQUIRED_ITEM").with(MapleItemInformationProvider.getInstance().getName(toCreate)));
                  PacketCreator.announce(client, new MakerEnableActions());
                  break;
               case 2: // no meso
                  MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("MAKER_SKILL_MINIMUM_MESO_ERROR").with(GameConstants.numberWithCommas(recipe.getCost())));
                  PacketCreator.announce(client, new MakerEnableActions());
                  break;
               case 3: // no req level
                  MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("MAKER_SKILL_MINIMUM_LEVEL_ERROR"));
                  PacketCreator.announce(client, new MakerEnableActions());
                  break;
               case 4: // no req skill level
                  MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("MAKER_SKILL_MINIMUM_MAKER_LEVEL_ERROR"));
                  PacketCreator.announce(client, new MakerEnableActions());
                  break;
               case 5: // inventory full
                  MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("MAKER_SKILL_INVENTORY_FULL_ERROR"));
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
                  if (stimulantId == -1 && reagentIds.isEmpty()) {
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

                     if (stimulantId != -1) {
                        client.getAbstractPlayerInteraction().gainItem(stimulantId, (short) -1, false);
                     }
                     if (!reagentIds.isEmpty()) {
                        for (Map.Entry<Integer, Short> r : reagentIds.entrySet()) {
                           client.getAbstractPlayerInteraction().gainItem(r.getKey(), (short) (-1 * r.getValue()), false);
                        }
                     }

                     if (cost > 0) {
                        client.getPlayer().gainMeso(-cost, false);
                     }
                     makerSucceeded = addBoostedMakerItem(client, toCreate, stimulantId, reagentIds);
                  }

                  if (type == 3) {
                     PacketCreator.announce(client, new MakerCrystalResult(recipe.getGainItems().get(0).getLeft(), recipe.getReqItems().get(0).getLeft()));
                  } else if (type == 4) {
                     PacketCreator.announce(client, new MakerResultDestroy(recipe.getReqItems().get(0).getLeft(), recipe.getCost(), recipe.getGainItems()));
                  } else {
                     PacketCreator.announce(client, new MakerResult(makerSucceeded, recipe.getGainItems().get(0).getLeft(), recipe.getGainItems().get(0).getRight(), recipe.getCost(), recipe.getReqItems(), stimulantId, new LinkedList<>(reagentIds.keySet())));
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
   private boolean removeOddMakerReagents(int toCreate, Map<Integer, Short> reagentIds) {
      Map<Integer, Integer> reagentType = new LinkedHashMap<>();
      List<Integer> toRemove = new LinkedList<>();

      boolean isWeapon = ItemConstants.isWeapon(toCreate) || YamlConfig.config.server.USE_MAKER_PERMISSIVE_ATKUP;

      for (Map.Entry<Integer, Short> r : reagentIds.entrySet()) {
         int curRid = r.getKey();
         int type = r.getKey() / 100;

         if (type < 42502 && !isWeapon) {     // only weapons should gain w.att/m.att from these.
            return false;
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
         reagentIds.remove(i);
      }

      // only quantity 1 of each gem will be accepted by the Maker skill
      reagentIds.replaceAll((i, v) -> (short) 1);
      return true;
   }

   private short getCreateStatus(MapleClient c, MakerItemFactory.MakerItemCreateEntry recipe) {
      if (recipe.isInvalid()) {
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

      List<Integer> addItemIds = new LinkedList<>();
      List<Integer> addQuantity = new LinkedList<>();
      List<Integer> removeItemIds = new LinkedList<>();
      List<Integer> rmvQuantity = new LinkedList<>();

      for (Pair<Integer, Integer> p : recipe.getReqItems()) {
         removeItemIds.add(p.getLeft());
         rmvQuantity.add(p.getRight());
      }

      for (Pair<Integer, Integer> p : recipe.getGainItems()) {
         addItemIds.add(p.getLeft());
         addQuantity.add(p.getRight());
      }

      if (!c.getAbstractPlayerInteraction().canHoldAllAfterRemoving(addItemIds, addQuantity, removeItemIds, rmvQuantity)) {
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

   private boolean addBoostedMakerItem(MapleClient c, int itemId, int stimulantId, Map<Integer, Short> reagentIds) {
      if (stimulantId != -1 && !MapleItemInformationProvider.rollSuccessChance(90.0)) {
         return false;
      }

      Item item = MapleItemInformationProvider.getInstance().getEquipById(itemId);
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

      if (!reagentIds.isEmpty()) {
         Map<String, Integer> stats = new LinkedHashMap<>();
         List<Short> randOption = new LinkedList<>();
         List<Short> randStat = new LinkedList<>();

         for (Map.Entry<Integer, Short> r : reagentIds.entrySet()) {
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

      if (stimulantId != -1) {
         eqp = MapleItemInformationProvider.getInstance().randomizeUpgradeStats(eqp);
      }

      MapleInventoryManipulator.addFromDrop(c, item, false, -1);
      return true;
   }
}
