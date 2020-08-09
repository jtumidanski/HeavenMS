package net.server.channel.packet.cash.use;

public class UseVegaSpellPacket extends AbstractUseCashItemPacket {
   private final Boolean firstCheck;

   private final Byte equipSlot;

   private final Boolean secondCheck;

   private final Byte useSlot;

   public UseVegaSpellPacket(Short position, Integer itemId, Boolean firstCheck, Byte equipSlot, Boolean secondCheck, Byte useSlot) {
      super(position, itemId);
      this.firstCheck = firstCheck;
      this.equipSlot = equipSlot;
      this.secondCheck = secondCheck;
      this.useSlot = useSlot;
   }

   public Boolean firstCheck() {
      return firstCheck;
   }

   public Byte equipSlot() {
      return equipSlot;
   }

   public Boolean secondCheck() {
      return secondCheck;
   }

   public Byte useSlot() {
      return useSlot;
   }
}
