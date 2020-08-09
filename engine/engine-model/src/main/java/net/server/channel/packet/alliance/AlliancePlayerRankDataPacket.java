package net.server.channel.packet.alliance;

public class AlliancePlayerRankDataPacket extends AllianceOperationPacket {
   private final int playerId;

   private final boolean rankRaised;

   public AlliancePlayerRankDataPacket(int playerId, boolean rankRaised) {
      this.playerId = playerId;
      this.rankRaised = rankRaised;
   }

   public int playerId() {
      return playerId;
   }

   public boolean rankRaised() {
      return rankRaised;
   }
}
