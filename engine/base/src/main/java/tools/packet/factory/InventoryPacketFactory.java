package tools.packet.factory;

import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MaplePet;
import client.inventory.ModifyInventory;
import constants.ExpTable;
import constants.ItemConstants;
import net.opcodes.SendOpcode;
import server.MapleItemInformationProvider;
import tools.StringUtil;
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
      registry.setHandler(ModifyInventoryPacket.class, packet -> create(SendOpcode.INVENTORY_OPERATION, this::modifyInventory, packet));
      registry.setHandler(InventoryFull.class, packet -> create(SendOpcode.INVENTORY_OPERATION, this::modifyInventory,
            new ModifyInventoryPacket(((InventoryFull) packet).updateTick(), ((InventoryFull) packet).modifications())));
      registry.setHandler(SlotLimitUpdate.class, packet -> create(SendOpcode.INVENTORY_GROW, this::updateInventorySlotLimit, packet));
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

   protected void addItemInfo(final MaplePacketLittleEndianWriter writer, Item item, boolean zeroPosition) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      boolean isCash = ii.isCash(item.id());
      boolean isPet = item.petId() > -1;
      boolean isRing = false;
      Equip equip = null;
      short pos = item.position();
      byte itemType = item.itemType();
      if (itemType == 1) {
         equip = (Equip) item;
         isRing = equip.ringId() > -1;
      }
      if (!zeroPosition) {
         if (equip != null) {
            if (pos < 0) {
               pos *= -1;
            }
            writer.writeShort(pos > 100 ? pos - 100 : pos);
         } else {
            writer.write(pos);
         }
      }
      writer.write(itemType);
      writer.writeInt(item.id());
      writer.writeBool(isCash);
      if (isCash) {
         writer.writeLong(isPet ? item.petId() : isRing ? equip.ringId() : item.cashId());
      }
      addExpirationTime(writer, item.expiration());
      if (isPet) {
         MaplePet pet = item.pet().get();
         writer.writeAsciiString(StringUtil.getRightPaddedStr(pet.name(), '\0', 13));
         writer.write(pet.level());
         writer.writeShort(pet.closeness());
         writer.write(pet.fullness());
         addExpirationTime(writer, item.expiration());
         writer.writeInt(pet.petFlag());  /* pet flags found by -- lrenex & Spoon */

         writer.write(new byte[]{(byte) 0x50, (byte) 0x46}); //wonder what this is
         writer.writeInt(0);
         return;
      }
      if (equip == null) {
         writer.writeShort(item.quantity());
         writer.writeMapleAsciiString(item.owner());
         writer.writeShort(item.flag()); // flag

         if (ItemConstants.isRechargeable(item.id())) {
            writer.writeInt(2);
            writer.write(new byte[]{(byte) 0x54, 0, 0, (byte) 0x34});
         }
         return;
      }
      writer.write(equip.slots()); // upgrade slots
      writer.write(equip.level()); // level
      writer.writeShort(equip.str()); // str
      writer.writeShort(equip.dex()); // dex
      writer.writeShort(equip._int()); // int
      writer.writeShort(equip.luk()); // luk
      writer.writeShort(equip.hp()); // hp
      writer.writeShort(equip.mp()); // mp
      writer.writeShort(equip.watk()); // watk
      writer.writeShort(equip.matk()); // matk
      writer.writeShort(equip.wdef()); // wdef
      writer.writeShort(equip.mdef()); // mdef
      writer.writeShort(equip.acc()); // accuracy
      writer.writeShort(equip.avoid()); // avoid
      writer.writeShort(equip.hands()); // hands
      writer.writeShort(equip.speed()); // speed
      writer.writeShort(equip.jump()); // jump
      writer.writeMapleAsciiString(equip.owner()); // owner name
      writer.writeShort(equip.flag()); //Item Flags

      if (isCash) {
         for (int i = 0; i < 10; i++) {
            writer.write(0x40);
         }
      } else {
         int itemLevel = equip.itemLevel();

         long expNibble = (long) (ExpTable.getExpNeededForLevel(ii.getEquipLevelReq(item.id())) * equip.itemExp());
         expNibble /= ExpTable.getEquipExpNeededForLevel(itemLevel);

         writer.write(0);
         writer.write(itemLevel); //Item Level
         writer.writeInt((int) expNibble);
         writer.writeInt(equip.vicious()); //WTF NEXON ARE YOU SERIOUS?
         writer.writeLong(0);
      }
      writer.writeLong(getTime(-2));
      writer.writeInt(-1);
   }

   protected void updateInventorySlotLimit(MaplePacketLittleEndianWriter writer, SlotLimitUpdate packet) {
      writer.write(packet.inventoryType());
      writer.write(packet.newLimit());
   }
}