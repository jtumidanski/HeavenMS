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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import client.MapleClient;
import client.database.administrator.AccountAdministrator;
import client.database.administrator.MtsCartAdministrator;
import client.database.administrator.MtsItemAdministrator;
import client.database.provider.CharacterProvider;
import client.database.provider.MtsCartProvider;
import client.database.provider.MtsItemProvider;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import constants.ItemConstants;
import net.AbstractMaplePacketHandler;
import net.server.Server;
import net.server.channel.Channel;
import server.MTSItemInfo;
import server.MapleItemInformationProvider;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.data.input.SeekableLittleEndianAccessor;

public final class MTSHandler extends AbstractMaplePacketHandler {

   private static byte[] getMTS(int tab, int type, int page) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> {
         List<MTSItemInfo> items = new ArrayList<>();
         long pages;
         if (type != 0) {
            items.addAll(MtsItemProvider.getInstance().getByTabAndType(connection, tab, type, page * 16));
         } else {
            items.addAll(MtsItemProvider.getInstance().getByTab(connection, tab, page * 16));
         }

         long count;
         if (type != 0) {
            count = MtsItemProvider.getInstance().countByTabAndType(connection, tab, type);
         } else {
            count = MtsItemProvider.getInstance().countByTab(connection, tab);
         }

         pages = count / 16;
         if (count % 16 > 0) {
            pages++;
         }
         return MaplePacketCreator.sendMTS(items, tab, type, page, (int) pages); // resniff
      }).orElseThrow();
   }

   @Override
   public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
      // TODO add karma-to-untradeable flag on sold items here

      if (!c.getPlayer().getCashShop().isOpened()) {
         return;
      }
      if (slea.available() > 0) {
         byte op = slea.readByte();
         if (op == 2) { //put item up for sale
            putItemUpForSale(slea, c);
         } else if (op == 3) { //send offer for wanted item
         } else if (op == 4) { //list wanted item
            listWantedItem(slea);
         } else if (op == 5) { //change page
            changePage(slea, c);
         } else if (op == 6) { //search
            search(slea, c);
         } else if (op == 7) { //cancel sale
            cancelSale(slea, c);
         } else if (op == 8) { //transfer item from transfer inv.
            transferItem(slea, c);
         } else if (op == 9) { //add to cart
            addToCart(slea, c);
         } else if (op == 10) { //delete from cart
            deleteFromCart(slea, c);
         } else if (op == 12) { //put item up for auction
         } else if (op == 13) { //cancel wanted cart thing
         } else if (op == 14) { //buy auction item now
         } else if (op == 16) { //buy
            buyItem(slea, c);
         } else if (op == 17) { //buy from cart
            buyFromCart(slea, c);
         } else {
            System.out.println("Unhandled OP(MTS): " + op + " Packet: " + slea.toString());
         }
      } else {
         c.announce(MaplePacketCreator.showMTSCash(c.getPlayer()));
      }
   }


   //TODO - why is this different then MTSHandler.buyFromCart ?
   private void buyItem(SeekableLittleEndianAccessor slea, MapleClient c) {
      int id = slea.readInt(); //id of the item

      DatabaseConnection.getInstance().withConnection(connection ->
            MtsItemProvider.getInstance().getSaleInfoById(connection, id).ifPresentOrElse(info -> {
               int price = info.getRight() + 100 + (int) (info.getRight() * 0.1); //taxes
               if (c.getPlayer().getCashShop().getCash(4) >= price) { //FIX

                  boolean alwaysNull = true;

                  for (Channel cserv : Server.getInstance().getAllChannels()) {
                     int sellerId = info.getLeft();
                     int basePrice = info.getRight();

                     alwaysNull = cserv.getPlayerStorage().getCharacterById(sellerId).map(victim -> {
                        victim.getCashShop().gainCash(4, basePrice);
                        return false;
                     }).orElse(true);
                  }


                  if (alwaysNull) {
                     awardSellerCash(info.getLeft(), info.getRight());
                  }

                  transferFromCart(c, id, connection);
                  c.getPlayer().getCashShop().gainCash(4, -price);
                  c.enableCSActions();
                  c.announce(getMTS(c.getPlayer().getCurrentTab(), c.getPlayer().getCurrentType(), c.getPlayer().getCurrentPage()));
                  c.announce(MaplePacketCreator.MTSConfirmBuy());
                  c.announce(MaplePacketCreator.showMTSCash(c.getPlayer()));
                  c.announce(MaplePacketCreator.transferInventory(getTransfer(c.getPlayer().getId())));
                  c.announce(MaplePacketCreator.notYetSoldInv(getNotYetSold(c.getPlayer().getId())));
                  c.announce(MaplePacketCreator.enableActions());
               } else {
                  c.announce(MaplePacketCreator.MTSFailBuy());
               }
            }, () -> c.announce(MaplePacketCreator.MTSFailBuy())));
   }

   private void deleteFromCart(SeekableLittleEndianAccessor slea, MapleClient c) {
      int id = slea.readInt(); //id of the item
      DatabaseConnection.getInstance().withConnection(connection -> MtsCartAdministrator.getInstance().removeItemFromCart(connection, id, c.getPlayer().getId()));

      c.announce(getCart(c.getPlayer().getId()));
      c.enableCSActions();
      c.announce(MaplePacketCreator.transferInventory(getTransfer(c.getPlayer().getId())));
      c.announce(MaplePacketCreator.notYetSoldInv(getNotYetSold(c.getPlayer().getId())));
   }

   private void addToCart(SeekableLittleEndianAccessor slea, MapleClient c) {
      int id = slea.readInt(); //id of the item

      DatabaseConnection.getInstance().withConnection(connection -> {
         boolean itemForSaleBySomeoneElse = MtsItemProvider.getInstance().isItemForSaleBySomeoneElse(connection, id, c.getPlayer().getId());
         if (itemForSaleBySomeoneElse) {
            boolean itemAlreadyInCart = MtsCartProvider.getInstance().isItemInCart(connection, c.getPlayer().getId(), id);
            if (!itemAlreadyInCart) {
               MtsCartAdministrator.getInstance().addToCart(connection, c.getPlayer().getId(), id);
            }
         }

         c.announce(getMTS(c.getPlayer().getCurrentTab(), c.getPlayer().getCurrentType(), c.getPlayer().getCurrentPage()));
         c.enableCSActions();
         c.announce(MaplePacketCreator.enableActions());
         c.announce(MaplePacketCreator.transferInventory(getTransfer(c.getPlayer().getId())));
         c.announce(MaplePacketCreator.notYetSoldInv(getNotYetSold(c.getPlayer().getId())));
      });
   }

   private void transferItem(SeekableLittleEndianAccessor slea, MapleClient c) {
      int id = slea.readInt(); //id of the item

      DatabaseConnection.getInstance().withConnection(connection -> MtsItemProvider.getInstance().getTransferItem(connection, c.getPlayer().getId(), id)
            .ifPresent(item -> {
               item.setPosition(c.getPlayer().getInventory(ItemConstants.getInventoryType(item.getItemId())).getNextFreeSlot());
               MtsItemAdministrator.getInstance().deleteTransferItem(connection, id, c.getPlayer().getId());

               MapleInventoryManipulator.addFromDrop(c, item, false);
               c.enableCSActions();
               c.announce(getCart(c.getPlayer().getId()));
               c.announce(getMTS(c.getPlayer().getCurrentTab(), c.getPlayer().getCurrentType(), c.getPlayer().getCurrentPage()));
               c.announce(MaplePacketCreator.MTSConfirmTransfer(item.getQuantity(), item.getPosition()));
               c.announce(MaplePacketCreator.transferInventory(getTransfer(c.getPlayer().getId())));
            }));
   }

   private void cancelSale(SeekableLittleEndianAccessor slea, MapleClient c) {
      int id = slea.readInt(); //id of the item

      DatabaseConnection.getInstance().withConnection(connection -> {
         MtsItemAdministrator.getInstance().cancelSale(connection, c.getPlayer().getId(), id);
         MtsCartAdministrator.getInstance().removeItemFromCarts(connection, id);
      });

      c.enableCSActions();
      c.announce(getMTS(c.getPlayer().getCurrentTab(), c.getPlayer().getCurrentType(), c.getPlayer().getCurrentPage()));
      c.announce(MaplePacketCreator.notYetSoldInv(getNotYetSold(c.getPlayer().getId())));
      c.announce(MaplePacketCreator.transferInventory(getTransfer(c.getPlayer().getId())));
   }

   private void search(SeekableLittleEndianAccessor slea, MapleClient c) {
      int tab = slea.readInt();
      int type = slea.readInt();
      slea.readInt();
      int ci = slea.readInt();
      String search = slea.readMapleAsciiString();
      c.getPlayer().setSearch(search);
      c.getPlayer().changeTab(tab);
      c.getPlayer().changeType(type);
      c.getPlayer().changeCI(ci);
      c.enableCSActions();
      c.announce(MaplePacketCreator.enableActions());
      c.announce(getMTSSearch(tab, type, ci, search, c.getPlayer().getCurrentPage()));
      c.announce(MaplePacketCreator.showMTSCash(c.getPlayer()));
      c.announce(MaplePacketCreator.transferInventory(getTransfer(c.getPlayer().getId())));
      c.announce(MaplePacketCreator.notYetSoldInv(getNotYetSold(c.getPlayer().getId())));
   }

   private void listWantedItem(SeekableLittleEndianAccessor slea) {
      slea.readInt();
      slea.readInt();
      slea.readInt();
      slea.readShort();
      slea.readMapleAsciiString();
   }

   private void changePage(SeekableLittleEndianAccessor slea, MapleClient c) {
      int tab = slea.readInt();
      int type = slea.readInt();
      int page = slea.readInt();
      c.getPlayer().changePage(page);
      if (tab == 4 && type == 0) {
         c.announce(getCart(c.getPlayer().getId()));
      } else if (tab == c.getPlayer().getCurrentTab() && type == c.getPlayer().getCurrentType() && c.getPlayer().getSearch() != null) {
         c.announce(getMTSSearch(tab, type, c.getPlayer().getCurrentCI(), c.getPlayer().getSearch(), page));
      } else {
         c.getPlayer().setSearch(null);
         c.announce(getMTS(tab, type, page));
      }
      c.getPlayer().changeTab(tab);
      c.getPlayer().changeType(type);
      c.enableCSActions();
      c.announce(MaplePacketCreator.transferInventory(getTransfer(c.getPlayer().getId())));
      c.announce(MaplePacketCreator.notYetSoldInv(getNotYetSold(c.getPlayer().getId())));
   }

   private void putItemUpForSale(SeekableLittleEndianAccessor slea, MapleClient c) {
      byte itemtype = slea.readByte();
      int itemid = slea.readInt();
      slea.readShort();
      slea.skip(7);
      short stars = 1;
      if (itemtype == 1) {
         slea.skip(32);
      } else {
         stars = slea.readShort();
      }
      slea.readMapleAsciiString(); //another useless thing (owner)
      if (itemtype == 1) {
         slea.skip(32);
      } else {
         slea.readShort();
      }
      short slot;
      short quantity;
      if (itemtype != 1) {
         if (itemid / 10000 == 207 || itemid / 10000 == 233) {
            slea.skip(8);
         }
         slot = (short) slea.readInt();
      } else {
         slot = (short) slea.readInt();
      }
      if (itemtype != 1) {
         if (itemid / 10000 == 207 || itemid / 10000 == 233) {
            quantity = stars;
            slea.skip(4);
         } else {
            quantity = (short) slea.readInt();
         }
      } else {
         quantity = (byte) slea.readInt();
      }
      int price = slea.readInt();
      if (itemtype == 1) {
         quantity = 1;
      }
      if (quantity < 0 || price < 110 || c.getPlayer().getItemQuantity(itemid, false) < quantity) {
         return;
      }
      MapleInventoryType invType = ItemConstants.getInventoryType(itemid);
      Item i = c.getPlayer().getInventory(invType).getItem(slot).copy();
      if (i != null && c.getPlayer().getMeso() >= 5000) {

         final short postedQuantity = quantity;
         DatabaseConnection.getInstance().withConnection(connection -> {
            long itemForSaleCount = MtsItemProvider.getInstance().countBySeller(connection, c.getPlayer().getId());
            if (itemForSaleCount > 10) { //They have more than 10 items up for sale already!
               MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "You already have 10 items up for auction!");
               c.announce(getMTS(1, 0, 0));
               c.announce(MaplePacketCreator.transferInventory(getTransfer(c.getPlayer().getId())));
               c.announce(MaplePacketCreator.notYetSoldInv(getNotYetSold(c.getPlayer().getId())));
               return;
            }

            String date = buildDate();
            if (!i.getInventoryType().equals(MapleInventoryType.EQUIP)) {
               MtsItemAdministrator.getInstance().createItem(connection, 1, (int) invType.getType(), i.getItemId(),
                     postedQuantity, c.getPlayer().getId(), price, i.getOwner(), c.getPlayer().getName(), date);
            } else {
               Equip equip = (Equip) i;
               MtsItemAdministrator.getInstance().createEquip(connection, 1, (int) invType.getType(),
                     equip.getItemId(), postedQuantity, c.getPlayer().getId(), price, equip.getUpgradeSlots(),
                     equip.getLevel(), equip.getStr(), equip.getDex(), equip.getInt(), equip.getLuk(), equip.getHp(),
                     equip.getMp(), equip.getWatk(), equip.getMatk(), equip.getWdef(), equip.getMdef(), equip.getAcc(),
                     equip.getAvoid(), equip.getHands(), equip.getSpeed(), equip.getJump(), 0, equip.getOwner(),
                     c.getPlayer().getName(), date, equip.getVicious(), equip.getFlag());
            }
            MapleInventoryManipulator.removeFromSlot(c, invType, slot, postedQuantity, false);

            c.getPlayer().gainMeso(-5000, false);
            c.announce(MaplePacketCreator.MTSConfirmSell());
            c.announce(getMTS(1, 0, 0));
            c.enableCSActions();
            c.announce(MaplePacketCreator.transferInventory(getTransfer(c.getPlayer().getId())));
            c.announce(MaplePacketCreator.notYetSoldInv(getNotYetSold(c.getPlayer().getId())));
         });
      }
   }

   private String buildDate() {
      Calendar calendar = Calendar.getInstance();
      int year;
      int month;
      int day;
      int oldmax = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
      int oldday = calendar.get(Calendar.DAY_OF_MONTH) + 7;
      if (oldmax < oldday) {
         if (calendar.get(Calendar.MONTH) + 2 > 12) {
            year = calendar.get(Calendar.YEAR) + 1;
            month = 1;
            calendar.set(year, month, 1);
            day = oldday - oldmax;
         } else {
            month = calendar.get(Calendar.MONTH) + 2;
            year = calendar.get(Calendar.YEAR);
            calendar.set(year, month, 1);
            day = oldday - oldmax;
         }
      } else {
         day = calendar.get(Calendar.DAY_OF_MONTH) + 7;
         month = calendar.get(Calendar.MONTH);
         year = calendar.get(Calendar.YEAR);
      }
      String date = year + "-";
      if (month < 10) {
         date += "0" + month + "-";
      } else {
         date += month + "-";
      }
      if (day < 10) {
         date += "0" + day;
      } else {
         date += day + "";
      }
      return date;
   }

   private void transferFromCart(MapleClient c, int id, Connection con) {
      MtsItemAdministrator.getInstance().transfer(con, c.getPlayer().getId(), id);
      MtsCartAdministrator.getInstance().removeItemFromCarts(con, id);
   }

   private void buyFromCart(SeekableLittleEndianAccessor slea, MapleClient c) {
      int id = slea.readInt(); //id of the item

      DatabaseConnection.getInstance().withConnection(connection ->
            MtsItemProvider.getInstance().getSaleInfoById(connection, id).ifPresentOrElse(info -> {
               int price = info.getRight() + 100 + (int) (info.getRight() * 0.1);
               if (c.getPlayer().getCashShop().getCash(4) >= price) {

                  for (Channel channel : Server.getInstance().getAllChannels()) {
                     int sellerId = info.getLeft();
                     int basePrice = info.getRight();

                     channel.getPlayerStorage().getCharacterById(sellerId)
                           .ifPresentOrElse(victim -> victim.getCashShop().gainCash(4, basePrice),
                                 () -> awardSellerCash(sellerId, basePrice));
                  }
                  transferFromCart(c, id, connection);

                  c.getPlayer().getCashShop().gainCash(4, -price);
                  c.announce(getCart(c.getPlayer().getId()));
                  c.enableCSActions();
                  c.announce(MaplePacketCreator.MTSConfirmBuy());
                  c.announce(MaplePacketCreator.showMTSCash(c.getPlayer()));
                  c.announce(MaplePacketCreator.transferInventory(getTransfer(c.getPlayer().getId())));
                  c.announce(MaplePacketCreator.notYetSoldInv(getNotYetSold(c.getPlayer().getId())));
               } else {
                  c.announce(MaplePacketCreator.MTSFailBuy());
               }
            }, () -> c.announce(MaplePacketCreator.MTSFailBuy())));
   }

   private void awardSellerCash(int seller, int price) {
      DatabaseConnection.getInstance().withConnection(connection -> {
         int accountId = CharacterProvider.getInstance().getAccountIdForCharacterId(connection, seller);
         AccountAdministrator.getInstance().awardNxPrepaid(connection, accountId, price);
      });
   }

   public List<MTSItemInfo> getNotYetSold(int cid) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> MtsItemProvider.getInstance().getUnsoldItems(connection, cid)).orElseThrow();
   }

   public byte[] getCart(int cid) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> {
         List<MTSItemInfo> items = new ArrayList<>();
         MtsCartProvider.getInstance().getCartItems(connection, cid).forEach(itemId -> MtsItemProvider.getInstance().getById(connection, itemId).ifPresent(items::add));
         long cartSize = MtsCartProvider.getInstance().countCartSize(connection, cid);
         long pages = cartSize / 16;
         if (cartSize % 16 > 0) {
            pages += 1;
         }

         return MaplePacketCreator.sendMTS(items, 4, 0, 0, (int) pages);
      }).orElseThrow();
   }

   public List<MTSItemInfo> getTransfer(int cid) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> MtsItemProvider.getInstance().getTransferItems(connection, cid)).orElse(new ArrayList<>());
   }

   public byte[] getMTSSearch(int tab, int type, int characterId, String search, int page) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         List<MTSItemInfo> items = new ArrayList<>(MtsItemProvider.getInstance().getSearchItems(connection, tab, type, characterId, search, page, ii.getAllItems()));

         long searchCount = MtsItemProvider.getInstance().countSearchItems(connection, tab, type, characterId, search, ii.getAllItems());
         long pages = searchCount / 16;
         if (searchCount % 16 > 0) {
            pages++;
         }

         return MaplePacketCreator.sendMTS(items, tab, type, page, (int) pages);
      }).orElseThrow();
   }
}
