package net.server.channel.packet.mts;

public class SendOfferForWantedPacket extends BaseMTSPacket {
   public SendOfferForWantedPacket(Boolean available, Byte operation) {
      super(available, operation);
   }
}
