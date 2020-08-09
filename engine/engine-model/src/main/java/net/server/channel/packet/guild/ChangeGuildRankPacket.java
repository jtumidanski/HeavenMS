package net.server.channel.packet.guild;

public class ChangeGuildRankPacket extends BaseGuildOperationPacket {
   private final Integer playerId;

   private final Byte rank;

   public ChangeGuildRankPacket(Byte theType, Integer playerId, Byte rank) {
      super(theType);
      this.playerId = playerId;
      this.rank = rank;
   }

   public Integer playerId() {
      return playerId;
   }

   public Byte rank() {
      return rank;
   }
}
