package net.server.channel.packet.command.admin;

public class SummonMonsterPacket extends BaseAdminCommandPacket {
   private final Integer mobId;

   private final Integer quantity;

   public SummonMonsterPacket(Byte mode, Integer mobId, Integer quantity) {
      super(mode);
      this.mobId = mobId;
      this.quantity = quantity;
   }

   public Integer mobId() {
      return mobId;
   }

   public Integer quantity() {
      return quantity;
   }
}
