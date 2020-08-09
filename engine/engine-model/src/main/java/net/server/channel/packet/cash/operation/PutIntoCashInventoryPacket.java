package net.server.channel.packet.cash.operation;

public class PutIntoCashInventoryPacket extends BaseCashOperationPacket {
   private final Integer cashId;

   private final Byte invType;

   public PutIntoCashInventoryPacket(Integer action, Integer cashId, Byte invType) {
      super(action);
      this.cashId = cashId;
      this.invType = invType;
   }

   public Integer cashId() {
      return cashId;
   }

   public Byte invType() {
      return invType;
   }
}
