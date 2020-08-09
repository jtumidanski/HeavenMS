package net.server.channel.packet.interaction;

public class OpenCashPlayerInteractionPacket extends BasePlayerInteractionPacket {
   private final Integer birthday;

   public OpenCashPlayerInteractionPacket(Byte mode, Integer birthday) {
      super(mode);
      this.birthday = birthday;
   }

   public Integer birthday() {
      return birthday;
   }
}
