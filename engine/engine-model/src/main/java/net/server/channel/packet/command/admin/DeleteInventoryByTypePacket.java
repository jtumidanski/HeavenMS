package net.server.channel.packet.command.admin;

public class DeleteInventoryByTypePacket extends BaseAdminCommandPacket {
   private final Byte inventoryType;

   public DeleteInventoryByTypePacket(Byte mode, Byte inventoryType) {
      super(mode);
      this.inventoryType = inventoryType;
   }

   public Byte inventoryType() {
      return inventoryType;
   }
}
