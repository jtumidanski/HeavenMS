package net.server.channel.packet.command.admin;

public class PlayerWarnPacket extends BaseAdminCommandPacket {
   private final String victim;

   private final String message;

   public PlayerWarnPacket(Byte mode, String victim, String message) {
      super(mode);
      this.victim = victim;
      this.message = message;
   }

   public String victim() {
      return victim;
   }

   public String message() {
      return message;
   }
}
