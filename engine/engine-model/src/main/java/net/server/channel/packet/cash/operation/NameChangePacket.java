package net.server.channel.packet.cash.operation;

public class NameChangePacket extends BaseCashOperationPacket {
   private final Integer itemId;

   private final String oldName;

   private final String newName;

   public NameChangePacket(Integer action, Integer itemId, String oldName, String newName) {
      super(action);
      this.itemId = itemId;
      this.oldName = oldName;
      this.newName = newName;
   }

   public Integer itemId() {
      return itemId;
   }

   public String oldName() {
      return oldName;
   }

   public String newName() {
      return newName;
   }
}
