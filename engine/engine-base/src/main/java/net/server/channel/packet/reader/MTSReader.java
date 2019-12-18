package net.server.channel.packet.reader;

import net.server.PacketReader;
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
import tools.data.input.SeekableLittleEndianAccessor;

public class MTSReader implements PacketReader<BaseMTSPacket> {
   @Override
   public BaseMTSPacket read(SeekableLittleEndianAccessor accessor) {
      boolean available = accessor.available() > 0;
      byte operation = -1;
      if (available) {
         operation = accessor.readByte();

         if (operation == 2) {
            return readPlaceItemForSale(accessor, available, operation);
         } else if (operation == 3) {
            return readSendOfferForWanted(available, operation);
         } else if (operation == 4) {
            return readListWantedItem(accessor, available, operation);
         } else if (operation == 5) {
            return readChangePage(accessor, available, operation);
         } else if (operation == 6) {
            return readSearch(accessor, available, operation);
         } else if (operation == 7) {
            return readCancelSale(accessor, available, operation);
         } else if (operation == 8) {
            return readTransferItem(accessor, available, operation);
         } else if (operation == 9) {
            return readAddToCart(accessor, available, operation);
         } else if (operation == 10) {
            return readDeleteFromCart(accessor, available, operation);
         } else if (operation == 12) {
            return new PutItemUpForActionPacket(available, operation);
         } else if (operation == 13) {
            return new CancelWantedCartPacket(available, operation);
         } else if (operation == 14) {
            return new BuyAuctionItemNowPacket(available, operation);
         } else if (operation == 16) {
            return readBuyAuctionItem(accessor, available, operation);
         } else if (operation == 17) {
            return readBuyItemFromCart(accessor, available, operation);
         }
      }
      return new BaseMTSPacket(available, operation);
   }

   private BaseMTSPacket readBuyItemFromCart(SeekableLittleEndianAccessor accessor, boolean available, byte operation) {
      int id = accessor.readInt();
      return new BuyItemFromCartPacket(available, operation, id);
   }

   private BaseMTSPacket readBuyAuctionItem(SeekableLittleEndianAccessor accessor, boolean available, byte operation) {
      int id = accessor.readInt();
      return new BuyAuctionItemPacket(available, operation, id);
   }

   private BaseMTSPacket readDeleteFromCart(SeekableLittleEndianAccessor accessor, boolean available, byte operation) {
      int id = accessor.readInt();
      return new DeleteFromCartPacket(available, operation, id);
   }

   private BaseMTSPacket readAddToCart(SeekableLittleEndianAccessor accessor, boolean available, byte operation) {
      int itemId = accessor.readInt();
      return new AddToCartPacket(available, operation, itemId);
   }

   private BaseMTSPacket readTransferItem(SeekableLittleEndianAccessor accessor, boolean available, byte operation) {
      int itemId = accessor.readInt();
      return new TransferItemPacket(available, operation, itemId);
   }

   private BaseMTSPacket readCancelSale(SeekableLittleEndianAccessor accessor, boolean available, byte operation) {
      int itemId = accessor.readInt();
      return new CancelSalePacket(available, operation, itemId);
   }

   private BaseMTSPacket readSearch(SeekableLittleEndianAccessor accessor, boolean available, byte operation) {
      int tab = accessor.readInt();
      int type = accessor.readInt();
      accessor.readInt();
      int ci = accessor.readInt();
      String search = accessor.readMapleAsciiString();
      return new SearchPacket(available, operation, tab, type, ci, search);
   }

   private BaseMTSPacket readSendOfferForWanted(boolean available, byte operation) {
      return new SendOfferForWantedPacket(available, operation);
   }

   private BaseMTSPacket readChangePage(SeekableLittleEndianAccessor accessor, boolean available, byte operation) {
      int tab = accessor.readInt();
      int type = accessor.readInt();
      int page = accessor.readInt();
      return new ChangePagePacket(available, operation, tab, type, page);
   }

   private BaseMTSPacket readListWantedItem(SeekableLittleEndianAccessor accessor, boolean available, byte operation) {
      accessor.readInt();
      accessor.readInt();
      accessor.readInt();
      accessor.readShort();
      accessor.readMapleAsciiString();
      return new ListWantedItemPacket(available, operation);
   }

   private BaseMTSPacket readPlaceItemForSale(SeekableLittleEndianAccessor accessor, boolean available, byte operation) {
      byte itemType = accessor.readByte();
      int itemId = accessor.readInt();
      accessor.readShort();
      accessor.skip(7);
      short stars = 1;
      if (itemType == 1) {
         accessor.skip(32);
      } else {
         stars = accessor.readShort();
      }
      accessor.readMapleAsciiString(); //another useless thing (owner)
      if (itemType == 1) {
         accessor.skip(32);
      } else {
         accessor.readShort();
      }
      short slot;
      short quantity;
      if (itemType != 1) {
         if (itemId / 10000 == 207 || itemId / 10000 == 233) {
            accessor.skip(8);
         }
      }
      slot = (short) accessor.readInt();
      if (itemType != 1) {
         if (itemId / 10000 == 207 || itemId / 10000 == 233) {
            quantity = stars;
            accessor.skip(4);
         } else {
            quantity = (short) accessor.readInt();
         }
      } else {
         quantity = (byte) accessor.readInt();
      }
      int price = accessor.readInt();
      if (itemType == 1) {
         quantity = 1;
      }
      return new PlaceItemForSalePacket(available, operation, itemId, quantity, price, slot);
   }
}
