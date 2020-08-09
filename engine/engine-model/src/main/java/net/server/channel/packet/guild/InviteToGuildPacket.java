package net.server.channel.packet.guild;

public class InviteToGuildPacket extends BaseGuildOperationPacket {
   private final String name;

   public InviteToGuildPacket(Byte theType, String name) {
      super(theType);
      this.name = name;
   }

   public String name() {
      return name;
   }
}
