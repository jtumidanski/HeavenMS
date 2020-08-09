package net.server.channel.packet.command.admin;

public class HidePacket extends BaseAdminCommandPacket {
   private final Boolean hide;

   public HidePacket(Byte mode, Boolean hide) {
      super(mode);
      this.hide = hide;
   }

   public Boolean hide() {
      return hide;
   }
}
