package net.server.channel.packet.alliance;

public class ChangeAllianceLeaderPacket extends AllianceOperationPacket {
   private final int playerId;

   public ChangeAllianceLeaderPacket(int playerId) {
      this.playerId = playerId;
   }

   public int playerId() {
      return playerId;
   }
}
