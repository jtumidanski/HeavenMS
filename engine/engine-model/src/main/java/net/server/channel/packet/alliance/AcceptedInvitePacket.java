package net.server.channel.packet.alliance;

public class AcceptedInvitePacket extends AllianceOperationPacket {
   private final int allianceId;

   private final String recruitingGuild;

   public AcceptedInvitePacket(int allianceId, String recruitingGuild) {
      this.allianceId = allianceId;
      this.recruitingGuild = recruitingGuild;
   }

   public int allianceId() {
      return allianceId;
   }

   public String recruitingGuild() {
      return recruitingGuild;
   }
}
