package net.server.channel.packet.mts;

public class ListWantedItemPacket extends BaseMTSPacket {
   public ListWantedItemPacket(Boolean available, Byte operation) {
      super(available, operation);
   }
}
