package tools.packet.factory;

import client.inventory.ModifyInventory;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.inventory.InventoryFull;
import tools.packet.inventory.ModifyInventoryPacket;
import tools.packet.inventory.SlotLimitUpdate;

public class InventoryPacketFactory extends AbstractPacketFactory {
   private static InventoryPacketFactory instance;

   public static InventoryPacketFactory getInstance() {
      if (instance == null) {
         instance = new InventoryPacketFactory();
      }
      return instance;
   }

   private InventoryPacketFactory() {
      Handler.handle(ModifyInventoryPacket.class).decorate(this::modifyInventory).register(registry);
      Handler.handle(InventoryFull.class)
            .decorate((writer, packet) -> modifyInventory(writer, new ModifyInventoryPacket(packet.updateTick(), packet.modifications())))
            .register(registry);
      Handler.handle(SlotLimitUpdate.class).decorate(this::updateInventorySlotLimit).register(registry);
   }

   protected void modifyInventory(MaplePacketLittleEndianWriter writer, ModifyInventoryPacket packet) {
      writer.writeBool(packet.updateTick());
      writer.write(packet.modifications().size());
      //writer.write(0); v104 :)
      int addMovement = -1;
      for (ModifyInventory mod : packet.modifications()) {
         writer.write(mod.mode());
         writer.write(mod.inventoryType());
         writer.writeShort(mod.mode() == 2 ? mod.oldPos() : mod.position());
         switch (mod.mode()) {
            case 0: {//add item
               addItemInfo(writer, mod.item(), true);
               break;
            }
            case 1: {//update quantity
               writer.writeShort(mod.quantity());
               break;
            }
            case 2: {//move
               writer.writeShort(mod.position());
               if (mod.position() < 0 || mod.oldPos() < 0) {
                  addMovement = mod.oldPos() < 0 ? 1 : 2;
               }
               break;
            }
            case 3: {//remove
               if (mod.position() < 0) {
                  addMovement = 2;
               }
               break;
            }
         }
         mod.clear();
      }
      if (addMovement > -1) {
         writer.write(addMovement);
      }
   }

   protected void updateInventorySlotLimit(MaplePacketLittleEndianWriter writer, SlotLimitUpdate packet) {
      writer.write(packet.inventoryType());
      writer.write(packet.newLimit());
   }
}