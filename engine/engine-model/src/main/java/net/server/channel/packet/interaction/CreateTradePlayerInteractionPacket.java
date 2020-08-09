package net.server.channel.packet.interaction;

public class CreateTradePlayerInteractionPacket extends BaseCreatePlayerInteractionPacket {
   public CreateTradePlayerInteractionPacket(Byte mode, Byte createType) {
      super(mode, createType);
   }
}
