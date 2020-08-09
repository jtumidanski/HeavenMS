package net.server.channel.packet.command.admin;

public class SummonMonstersItemPacket extends BaseAdminCommandPacket {
   private final Integer summonItemId;

   public SummonMonstersItemPacket(Byte mode, Integer summonItemId) {
      super(mode);
      this.summonItemId = summonItemId;
   }

   public Integer summonItemId() {
      return summonItemId;
   }
}
