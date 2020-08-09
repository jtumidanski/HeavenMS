package net.server.channel.packet.cash.operation;

public class IncreaseInventorySlotsSmall extends BaseCashOperationPacket {
   private final Integer cash;

   private final Byte mode;

   private final Byte theType;

   public IncreaseInventorySlotsSmall(Integer action, Integer cash, Byte mode, Byte theType) {
      super(action);
      this.cash = cash;
      this.mode = mode;
      this.theType = theType;
   }

   public Integer cash() {
      return cash;
   }

   public Byte mode() {
      return mode;
   }

   public Byte theType() {
      return theType;
   }
}
