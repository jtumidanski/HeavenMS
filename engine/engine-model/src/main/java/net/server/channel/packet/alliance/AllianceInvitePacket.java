package net.server.channel.packet.alliance;

public class AllianceInvitePacket extends AllianceOperationPacket {
   private final String guildName;

   public AllianceInvitePacket(String guildName) {
      this.guildName = guildName;
   }

   public String guildName() {
      return guildName;
   }
}
