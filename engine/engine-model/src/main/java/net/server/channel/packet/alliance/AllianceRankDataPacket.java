package net.server.channel.packet.alliance;

public class AllianceRankDataPacket extends AllianceOperationPacket {
   private final String[] ranks;

   public AllianceRankDataPacket(String[] ranks) {
      this.ranks = ranks;
   }

   public String[] ranks() {
      return ranks;
   }
}
