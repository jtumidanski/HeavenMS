package net.server.channel.packet.cash.operation;

public class IncreaseStorageSlotsSmall extends BaseCashOperationPacket {
   private final Integer cash;

   private final Byte mode;

   public IncreaseStorageSlotsSmall(Integer action, Integer cash, Byte mode) {
      super(action);
      this.cash = cash;
      this.mode = mode;
   }

   public Integer cash() {
      return cash;
   }

   public Byte mode() {
      return mode;
   }
}
