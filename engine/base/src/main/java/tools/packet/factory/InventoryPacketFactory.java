package tools.packet.factory;

import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MaplePet;
import client.inventory.ModifyInventory;
import constants.ExpTable;
import constants.ItemConstants;
import net.opcodes.SendOpcode;
import server.MapleItemInformationProvider;
import tools.FilePrinter;
import tools.StringUtil;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.inventory.InventoryFull;
import tools.packet.inventory.ModifyInventoryPacket;
import tools.packet.PacketInput;
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
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof ModifyInventoryPacket) {
         return create(this::modifyInventory, packetInput);
      } else if (packetInput instanceof InventoryFull) {
         return create(this::modifyInventory, new ModifyInventoryPacket(((InventoryFull) packetInput).updateTick(), ((InventoryFull) packetInput).modifications()));
      } else if (packetInput instanceof SlotLimitUpdate) {
         return create(this::updateInventorySlotLimit, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] modifyInventory(ModifyInventoryPacket packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.INVENTORY_OPERATION.getValue());
      mplew.writeBool(packet.updateTick());
      mplew.write(packet.modifications().size());
      //mplew.write(0); v104 :)
      int addMovement = -1;
      for (ModifyInventory mod : packet.modifications()) {
         mplew.write(mod.mode());
         mplew.write(mod.inventoryType());
         mplew.writeShort(mod.mode() == 2 ? mod.oldPos() : mod.position());
         switch (mod.mode()) {
            case 0: {//add item
               addItemInfo(mplew, mod.item(), true);
               break;
            }
            case 1: {//update quantity
               mplew.writeShort(mod.quantity());
               break;
            }
            case 2: {//move
               mplew.writeShort(mod.position());
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
         mplew.write(addMovement);
      }
      return mplew.getPacket();
   }

   protected void addItemInfo(final MaplePacketLittleEndianWriter mplew, Item item, boolean zeroPosition) {
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
            mplew.writeShort(pos > 100 ? pos - 100 : pos);
         } else {
            mplew.write(pos);
         }
      }
      mplew.write(itemType);
      mplew.writeInt(item.id());
      mplew.writeBool(isCash);
      if (isCash) {
         mplew.writeLong(isPet ? item.petId() : isRing ? equip.ringId() : item.cashId());
      }
      addExpirationTime(mplew, item.expiration());
      if (isPet) {
         MaplePet pet = item.pet().get();
         mplew.writeAsciiString(StringUtil.getRightPaddedStr(pet.name(), '\0', 13));
         mplew.write(pet.level());
         mplew.writeShort(pet.closeness());
         mplew.write(pet.fullness());
         addExpirationTime(mplew, item.expiration());
         mplew.writeInt(pet.petFlag());  /* pet flags found by -- lrenex & Spoon */

         mplew.write(new byte[]{(byte) 0x50, (byte) 0x46}); //wonder what this is
         mplew.writeInt(0);
         return;
      }
      if (equip == null) {
         mplew.writeShort(item.quantity());
         mplew.writeMapleAsciiString(item.owner());
         mplew.writeShort(item.flag()); // flag

         if (ItemConstants.isRechargeable(item.id())) {
            mplew.writeInt(2);
            mplew.write(new byte[]{(byte) 0x54, 0, 0, (byte) 0x34});
         }
         return;
      }
      mplew.write(equip.slots()); // upgrade slots
      mplew.write(equip.level()); // level
      mplew.writeShort(equip.str()); // str
      mplew.writeShort(equip.dex()); // dex
      mplew.writeShort(equip._int()); // int
      mplew.writeShort(equip.luk()); // luk
      mplew.writeShort(equip.hp()); // hp
      mplew.writeShort(equip.mp()); // mp
      mplew.writeShort(equip.watk()); // watk
      mplew.writeShort(equip.matk()); // matk
      mplew.writeShort(equip.wdef()); // wdef
      mplew.writeShort(equip.mdef()); // mdef
      mplew.writeShort(equip.acc()); // accuracy
      mplew.writeShort(equip.avoid()); // avoid
      mplew.writeShort(equip.hands()); // hands
      mplew.writeShort(equip.speed()); // speed
      mplew.writeShort(equip.jump()); // jump
      mplew.writeMapleAsciiString(equip.owner()); // owner name
      mplew.writeShort(equip.flag()); //Item Flags

      if (isCash) {
         for (int i = 0; i < 10; i++) {
            mplew.write(0x40);
         }
      } else {
         int itemLevel = equip.itemLevel();

         long expNibble = (long) (ExpTable.getExpNeededForLevel(ii.getEquipLevelReq(item.id())) * equip.itemExp());
         expNibble /= ExpTable.getEquipExpNeededForLevel(itemLevel);

         mplew.write(0);
         mplew.write(itemLevel); //Item Level
         mplew.writeInt((int) expNibble);
         mplew.writeInt(equip.vicious()); //WTF NEXON ARE YOU SERIOUS?
         mplew.writeLong(0);
      }
      mplew.writeLong(getTime(-2));
      mplew.writeInt(-1);
   }

   protected byte[] updateInventorySlotLimit(SlotLimitUpdate packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.INVENTORY_GROW.getValue());
      mplew.write(packet.inventoryType());
      mplew.write(packet.newLimit());
      return mplew.getPacket();
   }
}