package net.server.channel.packet.cash.operation;

public class IncreaseCharacterSlotsPacket extends BaseCashOperationPacket {
   private final Integer cash;

   private final Integer itemId;

   public IncreaseCharacterSlotsPacket(Integer action, Integer cash, Integer itemId) {
      super(action);
      this.cash = cash;
      this.itemId = itemId;
   }

   public Integer cash() {
      return cash;
   }

   public Integer itemId() {
      return itemId;
   }
}
