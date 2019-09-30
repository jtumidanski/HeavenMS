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
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.Channel;
import net.server.channel.packet.mts.AddToCartPacket;
import net.server.channel.packet.mts.BaseMTSPacket;
import net.server.channel.packet.mts.BuyAuctionItemNowPacket;
import net.server.channel.packet.mts.BuyAuctionItemPacket;
import net.server.channel.packet.mts.BuyItemFromCartPacket;
import net.server.channel.packet.mts.CancelSalePacket;
import net.server.channel.packet.mts.CancelWantedCartPacket;
import net.server.channel.packet.mts.ChangePagePacket;
import net.server.channel.packet.mts.DeleteFromCartPacket;
import net.server.channel.packet.mts.ListWantedItemPacket;
import net.server.channel.packet.mts.PlaceItemForSalePacket;
import net.server.channel.packet.mts.PutItemUpForActionPacket;
import net.server.channel.packet.mts.SearchPacket;
import net.server.channel.packet.mts.SendOfferForWantedPacket;
import net.server.channel.packet.mts.TransferItemPacket;
import net.server.channel.packet.reader.MTSReader;
import server.MTSItemInfo;
import server.MapleItemInformationProvider;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.mtsoperation.GetNotYetSoldMTSInventory;
import tools.packet.mtsoperation.MTSConfirmBuy;
import tools.packet.mtsoperation.MTSConfirmSell;
import tools.packet.mtsoperation.MTSConfirmTransfer;
import tools.packet.mtsoperation.MTSFailBuy;
import tools.packet.mtsoperation.MTSTransferInventory;
import tools.packet.mtsoperation.SendMTS;
import tools.packet.mtsoperation.ShowMTSCash;
import tools.packet.stat.EnableActions;

//TODO this needs a lot more work.
public final class MTSHandler extends AbstractPacketHandler<BaseMTSPacket> {
   private byte[] getMTS(int tab, int type, int page) {
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
         return PacketCreator.create(new SendMTS(items, tab, type, page, (int) pages));
      }).orElseThrow();
   }

   @Override
   public Class<MTSReader> getReaderClass() {
      return MTSReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      return client.getPlayer().getCashShop().isOpened();
   }

   @Override
   public final void handlePacket(BaseMTSPacket packet, MapleClient c) {
      // TODO add karma-to-untradeable flag on sold items here

      if (packet.available()) {
         byte op = packet.operation();
         if (packet instanceof PlaceItemForSalePacket) {
            putItemUpForSale(c, ((PlaceItemForSalePacket) packet).quantity(), ((PlaceItemForSalePacket) packet).price(),
                  ((PlaceItemForSalePacket) packet).itemId(), ((PlaceItemForSalePacket) packet).slot());
         } else if (packet instanceof SendOfferForWantedPacket) {
         } else if (packet instanceof ListWantedItemPacket) {
         } else if (packet instanceof ChangePagePacket) {
            changePage(c, ((ChangePagePacket) packet).tab(), ((ChangePagePacket) packet).theType(),
                  ((ChangePagePacket) packet).page());
         } else if (packet instanceof SearchPacket) {
            search(c, ((SearchPacket) packet).tab(), ((SearchPacket) packet).theType(), ((SearchPacket) packet).ci(),
                  ((SearchPacket) packet).search());
         } else if (packet instanceof CancelSalePacket) {
            cancelSale(c, ((CancelSalePacket) packet).itemId());
         } else if (packet instanceof TransferItemPacket) {
            transferItem(c, ((TransferItemPacket) packet).itemId());
         } else if (packet instanceof AddToCartPacket) {
            addToCart(c, ((AddToCartPacket) packet).itemId());
         } else if (packet instanceof DeleteFromCartPacket) {
            deleteFromCart(c, ((DeleteFromCartPacket) packet).itemId());
         } else if (packet instanceof PutItemUpForActionPacket) {
         } else if (packet instanceof CancelWantedCartPacket) {
         } else if (packet instanceof BuyAuctionItemNowPacket) {
         } else if (packet instanceof BuyAuctionItemPacket) {
            buyItem(c, ((BuyAuctionItemPacket) packet).itemId());
         } else if (packet instanceof BuyItemFromCartPacket) {
            buyFromCart(c, ((BuyItemFromCartPacket) packet).itemId());
         } else {
            System.out.println("Unhandled OP(MTS): " + op + " Packet: " + packet.toString());
         }
      } else {
         PacketCreator.announce(c, new ShowMTSCash(c.getPlayer().getCashShop().getCash(2), c.getPlayer().getCashShop().getCash(4)));
      }
   }


   //TODO - why is this different then MTSHandler.buyFromCart ?
   private void buyItem(MapleClient c, int id) {
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
                  PacketCreator.announce(c, new MTSConfirmBuy());
                  PacketCreator.announce(c, new ShowMTSCash(c.getPlayer().getCashShop().getCash(2), c.getPlayer().getCashShop().getCash(4)));
                  PacketCreator.announce(c, new MTSTransferInventory(getTransfer(c.getPlayer().getId())));
                  PacketCreator.announce(c, new GetNotYetSoldMTSInventory(getNotYetSold(c.getPlayer().getId())));
                  PacketCreator.announce(c, new EnableActions());
               } else {
                  PacketCreator.announce(c, new MTSFailBuy());
               }
            }, () -> PacketCreator.announce(c, new MTSFailBuy())));
   }

   private void deleteFromCart(MapleClient c, int id) {
      DatabaseConnection.getInstance().withConnection(connection -> MtsCartAdministrator.getInstance().removeItemFromCart(connection, id, c.getPlayer().getId()));

      c.announce(getCart(c.getPlayer().getId()));
      c.enableCSActions();
      PacketCreator.announce(c, new MTSTransferInventory(getTransfer(c.getPlayer().getId())));
      PacketCreator.announce(c, new GetNotYetSoldMTSInventory(getNotYetSold(c.getPlayer().getId())));
   }

   private void addToCart(MapleClient c, int id) {
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
         PacketCreator.announce(c, new EnableActions());
         PacketCreator.announce(c, new MTSTransferInventory(getTransfer(c.getPlayer().getId())));
         PacketCreator.announce(c, new GetNotYetSoldMTSInventory(getNotYetSold(c.getPlayer().getId())));
      });
   }

   private void transferItem(MapleClient c, int id) {
      DatabaseConnection.getInstance().withConnection(connection -> MtsItemProvider.getInstance().getTransferItem(connection, c.getPlayer().getId(), id)
            .ifPresent(item -> {
               item.position_(c.getPlayer().getInventory(ItemConstants.getInventoryType(item.id())).getNextFreeSlot());
               MtsItemAdministrator.getInstance().deleteTransferItem(connection, id, c.getPlayer().getId());

               MapleInventoryManipulator.addFromDrop(c, item, false);
               c.enableCSActions();
               c.announce(getCart(c.getPlayer().getId()));
               c.announce(getMTS(c.getPlayer().getCurrentTab(), c.getPlayer().getCurrentType(), c.getPlayer().getCurrentPage()));
               PacketCreator.announce(c, new MTSConfirmTransfer(item.quantity(), item.position()));
               PacketCreator.announce(c, new MTSTransferInventory(getTransfer(c.getPlayer().getId())));
            }));
   }

   private void cancelSale(MapleClient c, int id) {
      DatabaseConnection.getInstance().withConnection(connection -> {
         MtsItemAdministrator.getInstance().cancelSale(connection, c.getPlayer().getId(), id);
         MtsCartAdministrator.getInstance().removeItemFromCarts(connection, id);
      });

      c.enableCSActions();
      c.announce(getMTS(c.getPlayer().getCurrentTab(), c.getPlayer().getCurrentType(), c.getPlayer().getCurrentPage()));
      PacketCreator.announce(c, new GetNotYetSoldMTSInventory(getNotYetSold(c.getPlayer().getId())));
      PacketCreator.announce(c, new MTSTransferInventory(getTransfer(c.getPlayer().getId())));
   }

   private void search(MapleClient c, int tab, int type, int ci, String search) {
      c.getPlayer().setSearch(search);
      c.getPlayer().changeTab(tab);
      c.getPlayer().changeType(type);
      c.getPlayer().changeCI(ci);
      c.enableCSActions();
      PacketCreator.announce(c, new EnableActions());
      c.announce(getMTSSearch(tab, type, ci, search, c.getPlayer().getCurrentPage()));
      PacketCreator.announce(c, new ShowMTSCash(c.getPlayer().getCashShop().getCash(2), c.getPlayer().getCashShop().getCash(4)));
      PacketCreator.announce(c, new MTSTransferInventory(getTransfer(c.getPlayer().getId())));
      PacketCreator.announce(c, new GetNotYetSoldMTSInventory(getNotYetSold(c.getPlayer().getId())));
   }

   private void changePage(MapleClient c, int tab, int type, int page) {
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
      PacketCreator.announce(c, new MTSTransferInventory(getTransfer(c.getPlayer().getId())));
      PacketCreator.announce(c, new GetNotYetSoldMTSInventory(getNotYetSold(c.getPlayer().getId())));
   }

   private void putItemUpForSale(MapleClient c, short quantity, int price, int itemId, short slot) {
      if (quantity < 0 || price < 110 || c.getPlayer().getItemQuantity(itemId, false) < quantity) {
         return;
      }
      MapleInventoryType invType = ItemConstants.getInventoryType(itemId);
      Item i = c.getPlayer().getInventory(invType).getItem(slot).copy();
      if (i != null && c.getPlayer().getMeso() >= 5000) {

         final short postedQuantity = quantity;
         DatabaseConnection.getInstance().withConnection(connection -> {
            long itemForSaleCount = MtsItemProvider.getInstance().countBySeller(connection, c.getPlayer().getId());
            if (itemForSaleCount > 10) { //They have more than 10 items up for sale already!
               MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "You already have 10 items up for auction!");
               c.announce(getMTS(1, 0, 0));
               PacketCreator.announce(c, new MTSTransferInventory(getTransfer(c.getPlayer().getId())));
               PacketCreator.announce(c, new GetNotYetSoldMTSInventory(getNotYetSold(c.getPlayer().getId())));
               return;
            }

            String date = buildDate();
            if (!i.inventoryType().equals(MapleInventoryType.EQUIP)) {
               MtsItemAdministrator.getInstance().createItem(connection, 1, invType.getType(), i.id(),
                     postedQuantity, i.expiration(), i.giftFrom(), c.getPlayer().getId(), price, i.owner(), c.getPlayer().getName(), date);
            } else {
               Equip equip = (Equip) i;
               MtsItemAdministrator.getInstance().createEquip(connection, 1, invType.getType(),
                     equip.id(), postedQuantity, equip.expiration(), equip.giftFrom(), c.getPlayer().getId(), price, equip.slots(),
                     equip.level(), equip.str(), equip.dex(), equip._int(), equip.luk(), equip.hp(),
                     equip.mp(), equip.watk(), equip.matk(), equip.wdef(), equip.mdef(), equip.acc(),
                     equip.avoid(), equip.hands(), equip.speed(), equip.jump(), 0, equip.owner(),
                     c.getPlayer().getName(), date, equip.vicious(), equip.flag(), equip.itemExp(),
                     equip.itemLevel(), equip.ringId());
            }
            MapleInventoryManipulator.removeFromSlot(c, invType, slot, postedQuantity, false);

            c.getPlayer().gainMeso(-5000, false);
            PacketCreator.announce(c, new MTSConfirmSell());
            c.announce(getMTS(1, 0, 0));
            c.enableCSActions();
            PacketCreator.announce(c, new MTSTransferInventory(getTransfer(c.getPlayer().getId())));
            PacketCreator.announce(c, new GetNotYetSoldMTSInventory(getNotYetSold(c.getPlayer().getId())));
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

   private void buyFromCart(MapleClient c, int id) {
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
                  PacketCreator.announce(c, new MTSConfirmBuy());
                  PacketCreator.announce(c, new ShowMTSCash(c.getPlayer().getCashShop().getCash(2), c.getPlayer().getCashShop().getCash(4)));
                  PacketCreator.announce(c, new MTSTransferInventory(getTransfer(c.getPlayer().getId())));
                  PacketCreator.announce(c, new GetNotYetSoldMTSInventory(getNotYetSold(c.getPlayer().getId())));
               } else {
                  PacketCreator.announce(c, new MTSFailBuy());
               }
            }, () -> PacketCreator.announce(c, new MTSFailBuy())));
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

         return PacketCreator.create(new SendMTS(items, 4, 0, 0, (int) pages));
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

         return PacketCreator.create(new SendMTS(items, tab, type, page, (int) pages));
      }).orElseThrow();
   }
}
