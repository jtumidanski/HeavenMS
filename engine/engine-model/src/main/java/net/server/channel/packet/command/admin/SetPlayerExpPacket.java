package net.server.channel.packet.command.admin;

public class SetPlayerExpPacket extends BaseAdminCommandPacket {
   private final Integer amount;

   public SetPlayerExpPacket(Byte mode, Integer amount) {
      super(mode);
      this.amount = amount;
   }

   public Integer amount() {
      return amount;
   }
}
