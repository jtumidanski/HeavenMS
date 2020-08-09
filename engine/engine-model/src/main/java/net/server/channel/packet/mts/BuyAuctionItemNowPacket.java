package net.server.channel.packet.mts;

public class BuyAuctionItemNowPacket extends BaseMTSPacket {
   public BuyAuctionItemNowPacket(Boolean available, Byte operation) {
      super(available, operation);
   }
}
