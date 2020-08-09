package net.server.channel.packet.command.admin;

public class KillMonsterPacket extends BaseAdminCommandPacket {
   private final Integer mobToKill;

   private final Integer amount;

   public KillMonsterPacket(Byte mode, Integer mobToKill, Integer amount) {
      super(mode);
      this.mobToKill = mobToKill;
      this.amount = amount;
   }

   public Integer mobToKill() {
      return mobToKill;
   }

   public Integer amount() {
      return amount;
   }
}
