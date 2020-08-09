package net.server.channel.packet.mts;

public class CancelWantedCartPacket extends BaseMTSPacket {
   public CancelWantedCartPacket(Boolean available, Byte operation) {
      super(available, operation);
   }
}
