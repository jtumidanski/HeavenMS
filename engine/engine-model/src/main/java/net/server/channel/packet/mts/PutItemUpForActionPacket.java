package net.server.channel.packet.mts;

public class PutItemUpForActionPacket extends BaseMTSPacket {
   public PutItemUpForActionPacket(Boolean available, Byte operation) {
      super(available, operation);
   }
}
