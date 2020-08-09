package net.server.channel.packet.guild;

public class CreateGuildPacket extends BaseGuildOperationPacket {
   private final String name;

   public CreateGuildPacket(Byte type, String name) {
      super(type);
      this.name = name;
   }

   public String name() {
      return name;
   }
}
