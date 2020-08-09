package net.server.channel.packet.interaction;

public class BanPlayerPlayerInteractionPacket extends BasePlayerInteractionPacket {
   private final String name;

   public BanPlayerPlayerInteractionPacket(Byte mode, String name) {
      super(mode);
      this.name = name;
   }

   public String name() {
      return name;
   }
}
