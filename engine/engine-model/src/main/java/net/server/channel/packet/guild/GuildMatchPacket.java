package net.server.channel.packet.guild;

public class GuildMatchPacket extends BaseGuildOperationPacket {
   private final Boolean result;

   public GuildMatchPacket(Byte theType, Boolean result) {
      super(theType);
      this.result = result;
   }

   public Boolean result() {
      return result;
   }
}
