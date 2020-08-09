package net.server.channel.packet.guild;

public class ExpelFromGuildPacket extends BaseGuildOperationPacket {
   private final Integer playerId;

   private final String name;

   public ExpelFromGuildPacket(Byte theType, Integer playerId, String name) {
      super(theType);
      this.playerId = playerId;
      this.name = name;
   }

   public Integer playerId() {
      return playerId;
   }

   public String name() {
      return name;
   }
}
