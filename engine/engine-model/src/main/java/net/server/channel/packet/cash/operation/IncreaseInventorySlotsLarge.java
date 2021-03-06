package net.server.channel.packet.cash.operation;

public class IncreaseInventorySlotsLarge extends BaseCashOperationPacket {
   private final Integer cash;

   private final Byte mode;

   private final Integer itemId;

   public IncreaseInventorySlotsLarge(Integer action, Integer cash, Byte mode, Integer itemId) {
      super(action);
      this.cash = cash;
      this.mode = mode;
      this.itemId = itemId;
   }

   public Integer cash() {
      return cash;
   }

   public Byte mode() {
      return mode;
   }

   public Integer itemId() {
      return itemId;
   }
}
