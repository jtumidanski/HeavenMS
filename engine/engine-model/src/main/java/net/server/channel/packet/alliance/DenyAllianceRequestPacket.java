package net.server.channel.packet.alliance;

public class DenyAllianceRequestPacket extends AllianceOperationPacket {
   private final String inviterName;

   private final String guildName;

   public DenyAllianceRequestPacket(String inviterName, String guildName) {
      this.inviterName = inviterName;
      this.guildName = guildName;
   }

   public String inviterName() {
      return inviterName;
   }

   public String guildName() {
      return guildName;
   }
}
