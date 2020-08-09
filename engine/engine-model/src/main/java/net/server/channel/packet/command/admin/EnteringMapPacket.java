package net.server.channel.packet.command.admin;

public class EnteringMapPacket extends BaseAdminCommandPacket {
   private final Byte theType;

   public EnteringMapPacket(Byte mode, Byte theType) {
      super(mode);
      this.theType = theType;
   }

   public Byte theType() {
      return theType;
   }
}
