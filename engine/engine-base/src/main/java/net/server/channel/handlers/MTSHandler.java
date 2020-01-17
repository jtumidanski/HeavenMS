package net.server.channel.handlers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Supplier;
import javax.persistence.EntityManager;

import client.MapleCharacter;
import client.MapleClient;
import database.administrator.AccountAdministrator;
import database.administrator.MtsCartAdministrator;
import database.administrator.MtsItemAdministrator;
import database.provider.CharacterProvider;
import database.provider.MtsCartProvider;
import database.provider.MtsItemProvider;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import constants.inventory.ItemConstants;
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
import server.CashShop;
import server.MTSItemInfo;
import server.MapleItemInformationProvider;
import database.DatabaseConnection;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.mtsoperation.GetNotYetSoldMTSInventory;
import tools.packet.mtsoperation.MTSConfirmBuy;
import tools.packet.mtsoperation.MTSConfirmSell;
import tools.packet.mtsoperation.MTSConfirmTransfer;
import tools.packet.mtsoperation.MTSFailBuy;
import tools.packet.mtsoperation.MTSTransferInventory;
import tools.packet.mtsoperation.SendMTS;
import tools.packet.mtsoperation.ShowMTSCash;
import tools.packet.stat.EnableActions;

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
   public final void handlePacket(BaseMTSPacket packet, MapleClient client) {
      // TODO add karma-to-untradeable flag on sold items here

      if (packet.available()) {
         byte op = packet.operation();
         if (packet instanceof PlaceItemForSalePacket) {
            putItemUpForSale(client, ((PlaceItemForSalePacket) packet).quantity(), ((PlaceItemForSalePacket) packet).price(),
                  ((PlaceItemForSalePacket) packet).itemId(), ((PlaceItemForSalePacket) packet).slot());
         } else if (packet instanceof SendOfferForWantedPacket) {
         } else if (packet instanceof ListWantedItemPacket) {
         } else if (packet instanceof ChangePagePacket) {
            changePage(client, ((ChangePagePacket) packet).tab(), ((ChangePagePacket) packet).theType(),
                  ((ChangePagePacket) packet).page());
         } else if (packet instanceof SearchPacket) {
            search(client, ((SearchPacket) packet).tab(), ((SearchPacket) packet).theType(), ((SearchPacket) packet).ci(),
                  ((SearchPacket) packet).search());
         } else if (packet instanceof CancelSalePacket) {
            cancelSale(client, ((CancelSalePacket) packet).itemId());
         } else if (packet instanceof TransferItemPacket) {
            transferItem(client, ((TransferItemPacket) packet).itemId());
         } else if (packet instanceof AddToCartPacket) {
            addToCart(client, ((AddToCartPacket) packet).itemId());
         } else if (packet instanceof DeleteFromCartPacket) {
            deleteFromCart(client, ((DeleteFromCartPacket) packet).itemId());
         } else if (packet instanceof PutItemUpForActionPacket) {
         } else if (packet instanceof CancelWantedCartPacket) {
         } else if (packet instanceof BuyAuctionItemNowPacket) {
         } else if (packet instanceof BuyAuctionItemPacket) {
            buyItem(client, ((BuyAuctionItemPacket) packet).itemId());
         } else if (packet instanceof BuyItemFromCartPacket) {
            buyFromCart(client, ((BuyItemFromCartPacket) packet).itemId());
         } else {
            System.out.println("Unhandled OP(MTS): " + op + " Packet: " + packet.toString());
         }
      } else {
         PacketCreator.announce(client, new ShowMTSCash(client.getPlayer().getCashShop().getCash(2), client.getPlayer().getCashShop().getCash(4)));
      }
   }

   private void buyItem(MapleClient client, int id) {
      genericBuy(client, id, () -> getMTS(client.getPlayer().getCurrentTab(), client.getPlayer().getCurrentType(), client.getPlayer().getCurrentPage()));
   }

   private void deleteFromCart(MapleClient client, int id) {
      DatabaseConnection.getInstance().withConnection(connection -> MtsCartAdministrator.getInstance().removeItemFromCart(connection, id, client.getPlayer().getId()));

      client.announce(getCart(client.getPlayer().getId()));
      client.enableCSActions();
      PacketCreator.announce(client, new MTSTransferInventory(getTransfer(client.getPlayer().getId())));
      PacketCreator.announce(client, new GetNotYetSoldMTSInventory(getNotYetSold(client.getPlayer().getId())));
   }

   private void addToCart(MapleClient client, int id) {
      DatabaseConnection.getInstance().withConnection(connection -> {
         boolean itemForSaleBySomeoneElse = MtsItemProvider.getInstance().isItemForSaleBySomeoneElse(connection, id, client.getPlayer().getId());
         if (itemForSaleBySomeoneElse) {
            boolean itemAlreadyInCart = MtsCartProvider.getInstance().isItemInCart(connection, client.getPlayer().getId(), id);
            if (!itemAlreadyInCart) {
               MtsCartAdministrator.getInstance().addToCart(connection, client.getPlayer().getId(), id);
            }
         }

         client.announce(getMTS(client.getPlayer().getCurrentTab(), client.getPlayer().getCurrentType(), client.getPlayer().getCurrentPage()));
         client.enableCSActions();
         PacketCreator.announce(client, new EnableActions());
         PacketCreator.announce(client, new MTSTransferInventory(getTransfer(client.getPlayer().getId())));
         PacketCreator.announce(client, new GetNotYetSoldMTSInventory(getNotYetSold(client.getPlayer().getId())));
      });
   }

   private void transferItem(MapleClient client, int id) {
      DatabaseConnection.getInstance().withConnection(connection -> MtsItemProvider.getInstance().getTransferItem(connection, client.getPlayer().getId(), id)
            .ifPresent(item -> {
               item.position_(client.getPlayer().getInventory(ItemConstants.getInventoryType(item.id())).getNextFreeSlot());
               MtsItemAdministrator.getInstance().deleteTransferItem(connection, id, client.getPlayer().getId());

               MapleInventoryManipulator.addFromDrop(client, item, false);
               client.enableCSActions();
               client.announce(getCart(client.getPlayer().getId()));
               client.announce(getMTS(client.getPlayer().getCurrentTab(), client.getPlayer().getCurrentType(), client.getPlayer().getCurrentPage()));
               PacketCreator.announce(client, new MTSConfirmTransfer(item.quantity(), item.position()));
               PacketCreator.announce(client, new MTSTransferInventory(getTransfer(client.getPlayer().getId())));
            }));
   }

   private void cancelSale(MapleClient client, int id) {
      DatabaseConnection.getInstance().withConnection(connection -> {
         MtsItemAdministrator.getInstance().cancelSale(connection, client.getPlayer().getId(), id);
         MtsCartAdministrator.getInstance().removeItemFromCarts(connection, id);
      });

      client.enableCSActions();
      client.announce(getMTS(client.getPlayer().getCurrentTab(), client.getPlayer().getCurrentType(), client.getPlayer().getCurrentPage()));
      PacketCreator.announce(client, new GetNotYetSoldMTSInventory(getNotYetSold(client.getPlayer().getId())));
      PacketCreator.announce(client, new MTSTransferInventory(getTransfer(client.getPlayer().getId())));
   }

   private void search(MapleClient client, int tab, int type, int ci, String search) {
      client.getPlayer().setSearch(search);
      client.getPlayer().changeTab(tab);
      client.getPlayer().changeType(type);
      client.getPlayer().changeCI(ci);
      client.enableCSActions();
      PacketCreator.announce(client, new EnableActions());
      client.announce(getMTSSearch(tab, type, ci, search, client.getPlayer().getCurrentPage()));
      PacketCreator.announce(client, new ShowMTSCash(client.getPlayer().getCashShop().getCash(2), client.getPlayer().getCashShop().getCash(4)));
      PacketCreator.announce(client, new MTSTransferInventory(getTransfer(client.getPlayer().getId())));
      PacketCreator.announce(client, new GetNotYetSoldMTSInventory(getNotYetSold(client.getPlayer().getId())));
   }

   private void changePage(MapleClient client, int tab, int type, int page) {
      client.getPlayer().changePage(page);
      if (tab == 4 && type == 0) {
         client.announce(getCart(client.getPlayer().getId()));
      } else if (tab == client.getPlayer().getCurrentTab() && type == client.getPlayer().getCurrentType() && client.getPlayer().getSearch() != null) {
         client.announce(getMTSSearch(tab, type, client.getPlayer().getCurrentCI(), client.getPlayer().getSearch(), page));
      } else {
         client.getPlayer().setSearch(null);
         client.announce(getMTS(tab, type, page));
      }
      client.getPlayer().changeTab(tab);
      client.getPlayer().changeType(type);
      client.enableCSActions();
      PacketCreator.announce(client, new MTSTransferInventory(getTransfer(client.getPlayer().getId())));
      PacketCreator.announce(client, new GetNotYetSoldMTSInventory(getNotYetSold(client.getPlayer().getId())));
   }

   private void putItemUpForSale(MapleClient client, short quantity, int price, int itemId, short slot) {
      if (quantity < 0 || price < 110 || client.getPlayer().getItemQuantity(itemId, false) < quantity) {
         return;
      }
      MapleInventoryType invType = ItemConstants.getInventoryType(itemId);
      Item i = client.getPlayer().getInventory(invType).getItem(slot).copy();
      if (i != null && client.getPlayer().getMeso() >= 5000) {

         final short postedQuantity = quantity;
         DatabaseConnection.getInstance().withConnection(connection -> {
            long itemForSaleCount = MtsItemProvider.getInstance().countBySeller(connection, client.getPlayer().getId());
            if (itemForSaleCount > 10) { //They have more than 10 items up for sale already!
               MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("MTS_MAXIMUM_ITEMS_FOR_SALE"));
               client.announce(getMTS(1, 0, 0));
               PacketCreator.announce(client, new MTSTransferInventory(getTransfer(client.getPlayer().getId())));
               PacketCreator.announce(client, new GetNotYetSoldMTSInventory(getNotYetSold(client.getPlayer().getId())));
               return;
            }

            String date = buildDate();
            if (!i.inventoryType().equals(MapleInventoryType.EQUIP)) {
               MtsItemAdministrator.getInstance().createItem(connection, 1, invType.getType(), i.id(),
                     postedQuantity, i.expiration(), i.giftFrom(), client.getPlayer().getId(), price, i.owner(), client.getPlayer().getName(), date);
            } else {
               Equip equip = (Equip) i;
               MtsItemAdministrator.getInstance().createEquip(connection, 1, invType.getType(),
                     equip.id(), postedQuantity, equip.expiration(), equip.giftFrom(), client.getPlayer().getId(), price, equip.slots(),
                     equip.level(), equip.str(), equip.dex(), equip._int(), equip.luk(), equip.hp(),
                     equip.mp(), equip.watk(), equip.matk(), equip.wdef(), equip.mdef(), equip.acc(),
                     equip.avoid(), equip.hands(), equip.speed(), equip.jump(), 0, equip.owner(),
                     client.getPlayer().getName(), date, equip.vicious(), equip.flag(), equip.itemExp(),
                     equip.itemLevel(), equip.ringId());
            }
            MapleInventoryManipulator.removeFromSlot(client, invType, slot, postedQuantity, false);

            client.getPlayer().gainMeso(-5000, false);
            PacketCreator.announce(client, new MTSConfirmSell());
            client.announce(getMTS(1, 0, 0));
            client.enableCSActions();
            PacketCreator.announce(client, new MTSTransferInventory(getTransfer(client.getPlayer().getId())));
            PacketCreator.announce(client, new GetNotYetSoldMTSInventory(getNotYetSold(client.getPlayer().getId())));
         });
      }
   }

   private String buildDate() {
      Calendar calendar = Calendar.getInstance();
      int year;
      int month;
      int day;
      int oldMax = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
      int oldDay = calendar.get(Calendar.DAY_OF_MONTH) + 7;
      if (oldMax < oldDay) {
         if (calendar.get(Calendar.MONTH) + 2 > 12) {
            year = calendar.get(Calendar.YEAR) + 1;
            month = 1;
            calendar.set(year, month, 1);
            day = oldDay - oldMax;
         } else {
            month = calendar.get(Calendar.MONTH) + 2;
            year = calendar.get(Calendar.YEAR);
            calendar.set(year, month, 1);
            day = oldDay - oldMax;
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

   private void transferFromCart(MapleClient client, int id, EntityManager entityManager) {
      MtsItemAdministrator.getInstance().transfer(entityManager, client.getPlayer().getId(), id);
      MtsCartAdministrator.getInstance().removeItemFromCarts(entityManager, id);
   }

   protected void genericBuy(MapleClient client, int id, Supplier<byte[]> announce) {
      DatabaseConnection.getInstance().withConnection(connection ->
            MtsItemProvider.getInstance().getSaleInfoById(connection, id).ifPresentOrElse(info -> {
               int price = info.getRight() + 100 + (int) (info.getRight() * 0.1);
               MapleCharacter character = client.getPlayer();
               CashShop cashShop = character.getCashShop();

               if (cashShop.getCash(4) >= price) {

                  boolean alwaysNull = true;

                  for (Channel channel : Server.getInstance().getAllChannels()) {
                     int sellerId = info.getLeft();
                     int basePrice = info.getRight();

                     alwaysNull = channel.getPlayerStorage()
                           .getCharacterById(sellerId)
                           .map(victim -> {
                              victim.getCashShop().gainCash(4, basePrice);
                              return false;
                           })
                           .orElse(true);
                  }

                  if (alwaysNull) {
                     awardSellerCash(info.getLeft(), info.getRight());
                  }

                  transferFromCart(client, id, connection);

                  character.getCashShop().gainCash(4, -price);
                  client.announce(announce.get());
                  client.enableCSActions();
                  PacketCreator.announce(client, new MTSConfirmBuy());
                  PacketCreator.announce(client, new ShowMTSCash(cashShop.getCash(2), cashShop.getCash(4)));
                  PacketCreator.announce(client, new MTSTransferInventory(getTransfer(character.getId())));
                  PacketCreator.announce(client, new GetNotYetSoldMTSInventory(getNotYetSold(character.getId())));
               } else {
                  PacketCreator.announce(client, new MTSFailBuy());
               }
            }, () -> PacketCreator.announce(client, new MTSFailBuy())));
   }

   private void buyFromCart(MapleClient client, int id) {
      genericBuy(client, id, () -> getCart(client.getPlayer().getId()));
   }

   private void awardSellerCash(int seller, int price) {
      DatabaseConnection.getInstance().withConnection(connection -> {
         int accountId = CharacterProvider.getInstance().getAccountIdForCharacterId(connection, seller);
         AccountAdministrator.getInstance().awardNxPrepaid(connection, accountId, price);
      });
   }

   public List<MTSItemInfo> getNotYetSold(int characterId) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> MtsItemProvider.getInstance().getUnsoldItems(connection, characterId)).orElseThrow();
   }

   public byte[] getCart(int characterId) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> {
         List<MTSItemInfo> items = new ArrayList<>();
         MtsCartProvider.getInstance().getCartItems(connection, characterId).forEach(itemId -> MtsItemProvider.getInstance().getById(connection, itemId).ifPresent(items::add));
         long cartSize = MtsCartProvider.getInstance().countCartSize(connection, characterId);
         long pages = cartSize / 16;
         if (cartSize % 16 > 0) {
            pages += 1;
         }

         return PacketCreator.create(new SendMTS(items, 4, 0, 0, (int) pages));
      }).orElseThrow();
   }

   public List<MTSItemInfo> getTransfer(int characterId) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> MtsItemProvider.getInstance().getTransferItems(connection, characterId)).orElse(new ArrayList<>());
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
