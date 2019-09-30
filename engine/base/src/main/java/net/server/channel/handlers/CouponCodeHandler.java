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
package net.server.channel.handlers;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.database.administrator.NxCodeAdministrator;
import client.database.data.NxCodeData;
import client.database.data.NxCodeItemData;
import client.database.provider.NxCodeItemProvider;
import client.database.provider.NxCodeProvider;
import client.inventory.Item;
import client.inventory.manipulator.MapleInventoryManipulator;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.CouponCodePacket;
import net.server.channel.packet.reader.CouponCodeReader;
import server.CashShop;
import server.MapleItemInformationProvider;
import tools.DatabaseConnection;
import tools.FilePrinter;
import tools.PacketCreator;
import tools.Pair;
import tools.packet.cashshop.operation.ShowBoughtQuestItem;
import tools.packet.cashshop.operation.ShowCashShopMessage;
import tools.packet.cashshop.operation.ShowCouponRedeemSuccess;
import tools.packet.cashshop.CashShopMessage;

/**
 * @author Penguins (Acrylic)
 * @author Ronan (HeavenMS)
 */
public final class CouponCodeHandler extends AbstractPacketHandler<CouponCodePacket> {

   private List<NxCodeItemData> getNXCodeItems(MapleCharacter chr, Connection con, int codeid) {
      Map<Integer, Integer> couponItems = new HashMap<>();
      Map<Integer, Integer> couponPoints = new HashMap<>(5);

      NxCodeItemProvider.getInstance().get(con, codeid).forEach(item -> {
         if (item.theType() < 5) {
            couponPoints.merge(item.theType(), item.quantity(), Integer::sum);
         } else {
            couponItems.merge(item.itemId(), item.quantity(), Integer::sum);
         }
      });

      List<NxCodeItemData> ret = new LinkedList<>();
      if (!couponItems.isEmpty()) {
         for (Entry<Integer, Integer> e : couponItems.entrySet()) {
            int item = e.getKey(), qty = e.getValue();

            if (MapleItemInformationProvider.getInstance().getName(item) == null) {
               item = 4000000;
               qty = 1;

               FilePrinter.printError(FilePrinter.UNHANDLED_EVENT, "Error trying to redeem itemid " + item + " from codeid " + codeid + ".");
            }

            if (!chr.canHold(item, qty)) {
               return null;
            }

            ret.add(new NxCodeItemData(5, qty, item));
         }
      }

      if (!couponPoints.isEmpty()) {
         for (Entry<Integer, Integer> e : couponPoints.entrySet()) {
            ret.add(new NxCodeItemData(e.getKey(), e.getValue(), 777));
         }
      }

      return ret;
   }

   private Pair<Integer, List<NxCodeItemData>> getNXCodeResult(MapleCharacter chr, String code) {
      MapleClient c = chr.getClient();

      if (!c.attemptCsCoupon()) {
         return new Pair<>(-5, null);
      }

      return DatabaseConnection.getInstance().withConnectionResult(connection -> {
         Pair<Integer, List<NxCodeItemData>> retVal;
         Optional<NxCodeData> nxCode = NxCodeProvider.getInstance().get(connection, code);
         if (nxCode.isEmpty()) {
            retVal = new Pair<>(-1, new ArrayList<>());
            return retVal;
         }

         if (nxCode.get().retriever() != null) {
            retVal = new Pair<>(-2, new ArrayList<>());
            return retVal;
         }

         if (nxCode.get().expiration() < Server.getInstance().getCurrentTime()) {
            retVal = new Pair<>(-3, new ArrayList<>());
            return retVal;
         }

         int codeid = nxCode.get().id();

         List<NxCodeItemData> ret = getNXCodeItems(chr, connection, codeid);
         if (ret == null) {
            retVal = new Pair<>(-4, new ArrayList<>());
            return retVal;
         }

         NxCodeAdministrator.getInstance().setRetriever(connection, codeid, chr.getName());
         c.resetCsCoupon();
         retVal = new Pair<>(0, ret);
         return retVal;
      }).orElse(null);
   }

   private int parseCouponResult(int res) {
      switch (res) {
         case -1:
            return 0xB0;

         case -2:
            return 0xB3;

         case -3:
            return 0xB2;

         case -4:
            return 0xBB;

         default:
            return 0xB1;
      }
   }

   @Override
   public Class<CouponCodeReader> getReaderClass() {
      return CouponCodeReader.class;
   }

   @Override
   public void handlePacket(CouponCodePacket packet, MapleClient client) {
      if (client.tryAcquireClient()) {
         try {
            Pair<Integer, List<NxCodeItemData>> codeRes = getNXCodeResult(client.getPlayer(), packet.code().toUpperCase());
            int type = codeRes.getLeft();
            if (type < 0) {
               PacketCreator.announce(client, new ShowCashShopMessage(CashShopMessage.fromValue(parseCouponResult(type))));
            } else {
               List<Item> cashItems = new LinkedList<>();
               List<Pair<Integer, Integer>> items = new LinkedList<>();
               int nxCredit = 0;
               int maplePoints = 0;
               int nxPrepaid = 0;
               int mesos = 0;

               for (NxCodeItemData codeItemData : codeRes.getRight()) {
                  type = codeItemData.theType();
                  int quantity = codeItemData.quantity();

                  CashShop cs = client.getPlayer().getCashShop();
                  switch (type) {
                     case 0:
                        client.getPlayer().gainMeso(quantity, false); //mesos
                        mesos += quantity;
                        break;
                     case 4:
                        cs.gainCash(1, quantity);    //nxCredit
                        nxCredit += quantity;
                        break;
                     case 1:
                        cs.gainCash(2, quantity);    //maplePoint
                        maplePoints += quantity;
                        break;
                     case 2:
                        cs.gainCash(4, quantity);    //nxPrepaid
                        nxPrepaid += quantity;
                        break;
                     case 3:
                        cs.gainCash(1, quantity);
                        nxCredit += quantity;
                        cs.gainCash(4, (quantity / 5000));
                        nxPrepaid += quantity / 5000;
                        break;

                     default:
                        int item = codeItemData.itemId();

                        short qty;
                        if (quantity > Short.MAX_VALUE) {
                           qty = Short.MAX_VALUE;
                        } else if (quantity < Short.MIN_VALUE) {
                           qty = Short.MIN_VALUE;
                        } else {
                           qty = (short) quantity;
                        }

                        if (MapleItemInformationProvider.getInstance().isCash(item)) {
                           Item it = CashShop.generateCouponItem(item, qty);

                           cs.addToInventory(it);
                           cashItems.add(it);
                        } else {
                           MapleInventoryManipulator.addById(client, item, qty, "", -1);
                           items.add(new Pair<>((int) qty, item));
                        }
                        break;
                  }
               }
               if (cashItems.size() > 255) {
                  List<Item> oldList = cashItems;
                  cashItems = Arrays.asList(new Item[255]);
                  int index = 0;
                  for (Item item : oldList) {
                     cashItems.set(index, item);
                     index++;
                  }
               }
               if (nxCredit != 0 || nxPrepaid != 0) { //coupon packet can only show maple points (afaik)
                  PacketCreator.announce(client, new ShowBoughtQuestItem(0));
               } else {
                  PacketCreator.announce(client, new ShowCouponRedeemSuccess(client.getAccID(), maplePoints, mesos, cashItems, items));
               }
               client.enableCSActions();
            }
         } finally {
            client.releaseClient();
         }
      }
   }
}
