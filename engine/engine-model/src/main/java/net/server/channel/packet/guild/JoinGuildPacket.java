package net.server.channel.packet.guild;

public class JoinGuildPacket extends BaseGuildOperationPacket {
   private final Integer guildId;

   private final Integer playerId;

   public JoinGuildPacket(Byte theType, Integer guildId, Integer playerId) {
      super(theType);
      this.guildId = guildId;
      this.playerId = playerId;
   }

   public Integer guildId() {
      return guildId;
   }

   public Integer playerId() {
      return playerId;
   }
}
