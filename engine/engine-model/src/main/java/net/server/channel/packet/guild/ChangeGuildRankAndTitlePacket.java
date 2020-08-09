package net.server.channel.packet.guild;

public class ChangeGuildRankAndTitlePacket extends BaseGuildOperationPacket {
   private final String[] ranks;

   public ChangeGuildRankAndTitlePacket(Byte theType, String[] ranks) {
      super(theType);
      this.ranks = ranks;
   }

   public String[] ranks() {
      return ranks;
   }
}
