package net.server.channel.packet.interaction;

public class BaseCreatePlayerInteractionPacket extends BasePlayerInteractionPacket {
   private final Byte createType;

   public BaseCreatePlayerInteractionPacket(Byte mode, Byte createType) {
      super(mode);
      this.createType = createType;
   }

   public Byte createType() {
      return createType;
   }
}
