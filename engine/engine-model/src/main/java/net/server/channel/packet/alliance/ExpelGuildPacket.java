package net.server.channel.packet.alliance;

public class ExpelGuildPacket extends AllianceOperationPacket {
   private final int guildId;

   private final int allianceId;

   public ExpelGuildPacket(int guildId, int allianceId) {
      this.guildId = guildId;
      this.allianceId = allianceId;
   }

   public int guildId() {
      return guildId;
   }

   public int allianceId() {
      return allianceId;
   }
}
