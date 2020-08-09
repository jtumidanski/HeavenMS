package net.server.channel.packet.command.admin;

public class MonsterHpPacket extends BaseAdminCommandPacket {
   private final Integer mobHp;

   public MonsterHpPacket(Byte mode, Integer mobHp) {
      super(mode);
      this.mobHp = mobHp;
   }

   public Integer mobHp() {
      return mobHp;
   }
}
