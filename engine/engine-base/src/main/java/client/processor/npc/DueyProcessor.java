/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    Copyleft (L) 2016 - 2018 RonanLana (HeavenMS)

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
package client.processor.npc;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import client.DueyAction;
import client.MapleCharacter;
import client.MapleClient;
import client.autoban.AutobanFactory;
import client.database.administrator.DueyPackageAdministrator;
import client.database.provider.CharacterProvider;
import client.database.provider.DueyPackageProvider;
import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.inventory.manipulator.MapleKarmaManipulator;
import client.processor.ItemProcessor;
import config.YamlConfig;
import constants.inventory.ItemConstants;
import net.server.channel.Channel;
import scala.Option;
import server.DueyPackage;
import server.MapleItemInformationProvider;
import server.MapleTradeUtil;
import tools.DatabaseConnection;
import tools.FilePrinter;
import tools.PacketCreator;
import tools.Pair;
import tools.packet.parcel.DueyParcelReceived;
import tools.packet.parcel.RemoveDueyItem;
import tools.packet.parcel.SendDuey;
import tools.packet.stat.EnableActions;

/**
 * @author RonanLana - synchronization of Duey modules
 */
public class DueyProcessor {

   private static Pair<Integer, Integer> getAccountCharacterIdFromCNAME(String name) {
      return DatabaseConnection.getInstance().withConnectionResultOpt(connection ->
            CharacterProvider.getInstance().getIdAndAccountIdForName(connection, name)).orElse(null);
   }

   private static void showDueyNotification(MapleClient c, MapleCharacter player) {
      DatabaseConnection.getInstance().withConnection(connection -> DueyPackageProvider.getInstance().get(connection, player.getId())
            .ifPresent(pair -> {
               DueyPackageAdministrator.getInstance().uncheck(connection, player.getId());
               PacketCreator.announce(c, new DueyParcelReceived(pair.getLeft(), pair.getRight() == 1));
            }));
   }

   private static void deletePackageFromInventoryDB(EntityManager entityManager, int packageId) {
      ItemFactory.DUEY.saveItems(new LinkedList<>(), packageId, entityManager);
   }

   private static void removePackageFromDB(int packageId) {
      DatabaseConnection.getInstance().withConnection(entityManager -> {
         entityManager.getTransaction().begin();
         DueyPackageAdministrator.getInstance().removePackage(entityManager, packageId);
         deletePackageFromInventoryDB(entityManager, packageId);
         entityManager.getTransaction().commit();
      });
   }

   private static List<DueyPackage> loadPackages(MapleCharacter chr) {
      return DatabaseConnection.getInstance().withConnectionResult(connection ->
            DueyPackageProvider.getInstance().getPackagesForReceiver(connection, chr.getId())).orElseThrow();
   }

   private static int createPackage(int mesos, String message, String sender, int toCid, boolean quick) {
      return DatabaseConnection.getInstance().withConnectionResult(connection ->
            DueyPackageAdministrator.getInstance().create(connection, toCid, sender, mesos, message, quick))
            .orElse(-1);
   }

   private static boolean insertPackageItem(int packageId, Item item) {
      Pair<Item, MapleInventoryType> dueyItem = new Pair<>(item, MapleInventoryType.getByType(item.itemType()));
      DatabaseConnection.getInstance().withConnection(connection -> ItemFactory.DUEY.saveItems(Collections.singletonList(dueyItem), packageId, connection));
      return true;
   }

   private static int addPackageItemFromInventory(int packageId, MapleClient c, byte invTypeId, short itemPos, short amount) {
      if (invTypeId > 0) {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

         MapleInventoryType invType = MapleInventoryType.getByType(invTypeId);
         MapleInventory inv = c.getPlayer().getInventory(invType);

         Item item;
         inv.lockInventory();
         try {
            item = inv.getItem(itemPos);
            if (item != null && item.quantity() >= amount) {
               if (ItemProcessor.getInstance().isUntradeable(item) || ii.isUnmerchable(item.id())) {
                  return -1;
               }

               if (ItemConstants.isRechargeable(item.id())) {
                  MapleInventoryManipulator.removeFromSlot(c, invType, itemPos, item.quantity(), true);
               } else {
                  MapleInventoryManipulator.removeFromSlot(c, invType, itemPos, amount, true, false);
               }

               item = item.copy();
            } else {
               return -2;
            }
         } finally {
            inv.unlockInventory();
         }

         MapleKarmaManipulator.toggleKarmaFlagToUntradeable(item);
         item.quantity_$eq(amount);

         if (!insertPackageItem(packageId, item)) {
            return 1;
         }
      }

      return 0;
   }

   public static void dueySendItem(MapleClient c, byte invTypeId, short itemPos, short amount, int sendMesos, String sendMessage, String recipient, boolean quick) {
      if (c.tryAcquireClient()) {
         try {
            int fee = MapleTradeUtil.getFee(sendMesos);
            if (!quick) {
               fee += 5000;
            } else if (!c.getPlayer().haveItem(5330000)) {
               AutobanFactory.PACKET_EDIT.alert(c.getPlayer(), c.getPlayer().getName() + " tried to packet edit with Quick Delivery on duey.");
               FilePrinter.printError(FilePrinter.EXPLOITS + c.getPlayer().getName() + ".txt", c.getPlayer().getName() + " tried to use duey with Quick Delivery, mesos " + sendMesos + " and amount " + amount);
               c.disconnect(true, false);
               return;
            }

            long finalcost = (long) sendMesos + fee;
            if (finalcost < 0 || finalcost > Integer.MAX_VALUE || (amount < 1 && sendMesos == 0)) {
               AutobanFactory.PACKET_EDIT.alert(c.getPlayer(), c.getPlayer().getName() + " tried to packet edit with duey.");
               FilePrinter.printError(FilePrinter.EXPLOITS + c.getPlayer().getName() + ".txt", c.getPlayer().getName() + " tried to use duey with mesos " + sendMesos + " and amount " + amount);
               c.disconnect(true, false);
               return;
            }

            Pair<Integer, Integer> accIdCid;
            if (c.getPlayer().getMeso() >= finalcost) {
               accIdCid = getAccountCharacterIdFromCNAME(recipient);
               int recipientAccId = accIdCid.getLeft();
               if (recipientAccId != -1) {
                  if (recipientAccId == c.getAccID()) {
                     PacketCreator.announce(c, new SendDuey(DueyAction.TOCLIENT_SEND_SAMEACC_ERROR));
                     return;
                  }
               } else {
                  PacketCreator.announce(c, new SendDuey(DueyAction.TOCLIENT_SEND_NAME_DOES_NOT_EXIST));
                  return;
               }
            } else {
               PacketCreator.announce(c, new SendDuey(DueyAction.TOCLIENT_SEND_NOT_ENOUGH_MESOS));
               return;
            }

            int recipientCid = accIdCid.getRight();
            if (recipientCid == -1) {
               PacketCreator.announce(c, new SendDuey(DueyAction.TOCLIENT_SEND_NAME_DOES_NOT_EXIST));
               return;
            }

            if (quick) {
               MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, 5330000, (short) 1, false, false);
            }

            int packageId = createPackage(sendMesos, sendMessage, c.getPlayer().getName(), recipientCid, quick);
            if (packageId == -1) {
               PacketCreator.announce(c, new SendDuey(DueyAction.TOCLIENT_SEND_ENABLE_ACTIONS));
               return;
            }
            c.getPlayer().gainMeso((int) -finalcost, false);

            int res = addPackageItemFromInventory(packageId, c, invTypeId, itemPos, amount);
            if (res == 0) {
               PacketCreator.announce(c, new SendDuey(DueyAction.TOCLIENT_SEND_SUCCESSFULLY_SENT));
            } else if (res > 0) {
               PacketCreator.announce(c, new SendDuey(DueyAction.TOCLIENT_SEND_ENABLE_ACTIONS));
            } else {
               PacketCreator.announce(c, new SendDuey(DueyAction.TOCLIENT_SEND_INCORRECT_REQUEST));
            }

            MapleClient rClient = null;
            int channel = c.getWorldServer().find(recipient);
            if (channel > -1) {
               Channel rcserv = c.getWorldServer().getChannel(channel);
               if (rcserv != null) {
                  rClient = rcserv.getPlayerStorage().getCharacterByName(recipient).map(MapleCharacter::getClient).orElse(null);
               }
            }

            if (rClient != null && rClient.isLoggedIn() && !rClient.getPlayer().isAwayFromWorld()) {
               showDueyNotification(rClient, rClient.getPlayer());
            }
         } finally {
            c.releaseClient();
         }
      }
   }

   public static void dueyRemovePackage(MapleClient c, int packageid, boolean playerRemove) {
      if (c.tryAcquireClient()) {
         try {
            removePackageFromDB(packageid);
            PacketCreator.announce(c, new RemoveDueyItem(playerRemove, packageid));
         } finally {
            c.releaseClient();
         }
      }
   }

   public static void dueyClaimPackage(MapleClient c, int packageId) {
      if (c.tryAcquireClient()) {
         try {
            Optional<DueyPackage> dueyPackage = DatabaseConnection.getInstance().withConnectionResult(connection ->
                  DueyPackageProvider.getInstance().getById(connection, packageId).orElse(null));

            if (dueyPackage.isEmpty()) {
               PacketCreator.announce(c, new SendDuey(DueyAction.TOCLIENT_RECV_UNKNOWN_ERROR));
               FilePrinter.printError(FilePrinter.EXPLOITS + c.getPlayer().getName() + ".txt", c.getPlayer().getName() + " tried to receive package from duey with id " + packageId);
               return;
            }

            DueyPackage dp = dueyPackage.get();

            if (dp.isDeliveringTime()) {
               PacketCreator.announce(c, new SendDuey(DueyAction.TOCLIENT_RECV_UNKNOWN_ERROR));
               return;
            }

            if (dp.item().isDefined()) {
               Item dpItem = dp.item().get();
               if (!c.getPlayer().canHoldMeso(dp.mesos())) {
                  PacketCreator.announce(c, new SendDuey(DueyAction.TOCLIENT_RECV_UNKNOWN_ERROR));
                  return;
               }

               if (!MapleInventoryManipulator.checkSpace(c, dpItem.id(), dpItem.quantity(), dpItem.owner())) {
                  int itemid = dpItem.id();
                  if (MapleItemInformationProvider.getInstance().isPickupRestricted(itemid) && c.getPlayer().getInventory(ItemConstants.getInventoryType(itemid)).findById(itemid) != null) {
                     PacketCreator.announce(c, new SendDuey(DueyAction.TOCLIENT_RECV_RECEIVER_WITH_UNIQUE));
                  } else {
                     PacketCreator.announce(c, new SendDuey(DueyAction.TOCLIENT_RECV_NO_FREE_SLOTS));
                  }

                  return;
               } else {
                  MapleInventoryManipulator.addFromDrop(c, dpItem, false);
               }
            }

            c.getPlayer().gainMeso(dp.mesos(), false);

            dueyRemovePackage(c, packageId, false);
         } finally {
            c.releaseClient();
         }
      }
   }

   public static void dueySendTalk(MapleClient c, boolean quickDelivery) {
      if (c.tryAcquireClient()) {
         try {
            long timeNow = System.currentTimeMillis();
            if (timeNow - c.getPlayer().getNpcCooldown() < YamlConfig.config.server.BLOCK_NPC_RACE_CONDT) {
               PacketCreator.announce(c, new EnableActions());
               return;
            }
            c.getPlayer().setNpcCooldown(timeNow);

            if (quickDelivery) {
               PacketCreator.announce(c, new SendDuey(DueyAction.SOMETHING));
            } else {
               PacketCreator.announce(c, new SendDuey(DueyAction.TOCLIENT_OPEN_DUEY, Option.apply(loadPackages(c.getPlayer()))));
            }
         } finally {
            c.releaseClient();
         }
      }
   }

   public static void dueyCreatePackage(Item item, int mesos, String sender, int recipientCid) {
      int packageId = createPackage(mesos, null, sender, recipientCid, false);
      if (packageId != -1) {
         insertPackageItem(packageId, item);
      }
   }

   public static void runDueyExpireSchedule() {
      DatabaseConnection.getInstance().withConnection(entityManager -> {
         Calendar c = Calendar.getInstance();
         c.add(Calendar.DATE, -30);
         Timestamp ts = new Timestamp(c.getTime().getTime());

         entityManager.getTransaction().begin();
         List<Integer> toRemove = DueyPackageProvider.getInstance().getPackagesAfter(entityManager, ts);
         toRemove.forEach(DueyProcessor::removePackageFromDB);
         DueyPackageAdministrator.getInstance().deletePackagesAfter(entityManager, ts);
         entityManager.getTransaction().commit();
      });
   }
}
