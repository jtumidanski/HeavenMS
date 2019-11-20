package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.cash.operation.BaseCashOperationPacket;
import net.server.channel.packet.cash.operation.CrushRingPacket;
import net.server.channel.packet.cash.operation.FriendshipRingPacket;
import net.server.channel.packet.cash.operation.IncreaseCharacterSlotsPacket;
import net.server.channel.packet.cash.operation.IncreaseInventorySlotsLarge;
import net.server.channel.packet.cash.operation.IncreaseInventorySlotsSmall;
import net.server.channel.packet.cash.operation.IncreaseStorageSlotsLarge;
import net.server.channel.packet.cash.operation.IncreaseStorageSlotsSmall;
import net.server.channel.packet.cash.operation.MakePurchasePacket;
import net.server.channel.packet.cash.operation.MesoCashItemPurchase;
import net.server.channel.packet.cash.operation.ModifyWishListPacket;
import net.server.channel.packet.cash.operation.NameChangePacket;
import net.server.channel.packet.cash.operation.PutIntoCashInventoryPacket;
import net.server.channel.packet.cash.operation.SendGiftPacket;
import net.server.channel.packet.cash.operation.TakeCashFromInventoryPacket;
import net.server.channel.packet.cash.operation.WorldTransferPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class CashOperationReader implements PacketReader<BaseCashOperationPacket> {
   @Override
   public BaseCashOperationPacket read(SeekableLittleEndianAccessor accessor) {
      final int action = accessor.readByte();
      if (action == 0x03 || action == 0x1E) {
         return readMakePurchase(accessor, action);
      } else if (action == 0x04) {
         return readSendGift(accessor, action);
      } else if (action == 0x05) {
         return readModifyWishList(accessor, action);
      } else if (action == 0x06) {
         return readIncreaseInventorySlots(accessor, action);
      } else if (action == 0x07) {
         return readIncreaseStorageSlots(accessor, action);
      } else if (action == 0x08) {
         return readIncreaseCharacterSlots(accessor, action);
      } else if (action == 0x0D) {
         return readTakeCashFromInventory(accessor, action);
      } else if (action == 0x0E) {
         return readPutIntoCashInventory(accessor, action);
      } else if (action == 0x1D) {
         return readCrushRing(accessor, action);
      } else if (action == 0x20) {
         return readMesoCashItemPurchase(accessor, action);
      } else if (action == 0x23) {
         return readFriendshipRing(accessor, action);
      } else if (action == 0x2E) {
         return readNameChange(accessor, action);
      } else if (action == 0x31) {
         return readWorldTransfer(accessor, action);
      }

      return new BaseCashOperationPacket(action);
   }

   private BaseCashOperationPacket readWorldTransfer(SeekableLittleEndianAccessor accessor, int action) {
      int itemId = accessor.readInt();
      int newWorldSelection = accessor.readInt();
      return new WorldTransferPacket(action, itemId, newWorldSelection);
   }

   private BaseCashOperationPacket readNameChange(SeekableLittleEndianAccessor accessor, int action) {
      int itemId = accessor.readInt();
      String oldName = accessor.readMapleAsciiString();
      String newName = accessor.readMapleAsciiString();
      return new NameChangePacket(action, itemId, oldName, newName);
   }

   private BaseCashOperationPacket readFriendshipRing(SeekableLittleEndianAccessor accessor, int action) {
      int birthday = accessor.readInt();
      int payment = accessor.readByte();
      accessor.skip(3); //0s
      int snID = accessor.readInt();
      String sentTo = accessor.readMapleAsciiString();
      int available = accessor.readShort() - 1;
      String text = accessor.readAsciiString(available);
      accessor.readByte();
      return new FriendshipRingPacket(action, birthday, payment, snID, sentTo, text);
   }

   private BaseCashOperationPacket readMesoCashItemPurchase(SeekableLittleEndianAccessor accessor, int action) {
      int sn = accessor.readInt();
      return new MesoCashItemPurchase(action, sn);
   }

   private BaseCashOperationPacket readCrushRing(SeekableLittleEndianAccessor accessor, int action) {
      int birthday = accessor.readInt();
      int toCharge = accessor.readInt();
      int SN = accessor.readInt();
      String recipientName = accessor.readMapleAsciiString();
      String text = accessor.readMapleAsciiString();
      return new CrushRingPacket(action, birthday, toCharge, SN, recipientName, text);
   }

   private BaseCashOperationPacket readPutIntoCashInventory(SeekableLittleEndianAccessor accessor, int action) {
      int cashId = accessor.readInt();
      accessor.skip(4);
      byte invType = accessor.readByte();
      return new PutIntoCashInventoryPacket(action, cashId, invType);
   }

   private BaseCashOperationPacket readTakeCashFromInventory(SeekableLittleEndianAccessor accessor, int action) {
      int itemId = accessor.readInt();
      return new TakeCashFromInventoryPacket(action, itemId);
   }

   private BaseCashOperationPacket readIncreaseCharacterSlots(SeekableLittleEndianAccessor accessor, int action) {
      accessor.skip(1);
      int cash = accessor.readInt();
      int itemId = accessor.readInt();
      return new IncreaseCharacterSlotsPacket(action, cash, itemId);
   }

   private BaseCashOperationPacket readIncreaseStorageSlots(SeekableLittleEndianAccessor accessor, int action) {
      accessor.skip(1);
      int cash = accessor.readInt();
      byte mode = accessor.readByte();
      if (mode == 0) {
         return new IncreaseStorageSlotsSmall(action, cash, mode);
      } else {
         return new IncreaseStorageSlotsLarge(action, cash, mode, accessor.readInt());
      }
   }

   private BaseCashOperationPacket readIncreaseInventorySlots(SeekableLittleEndianAccessor accessor, int action) {
      accessor.skip(1);
      int cash = accessor.readInt();
      byte mode = accessor.readByte();
      if (mode == 0) {
         return new IncreaseInventorySlotsSmall(action, cash, mode, accessor.readByte());
      } else {
         return new IncreaseInventorySlotsLarge(action, cash, mode, accessor.readInt());
      }
   }

   private BaseCashOperationPacket readModifyWishList(SeekableLittleEndianAccessor accessor, int action) {
      int[] sns = new int[10];
      for (byte i = 0; i < 10; i++) {
         sns[i] = accessor.readInt();
      }
      return new ModifyWishListPacket(action, sns);
   }

   private BaseCashOperationPacket readSendGift(SeekableLittleEndianAccessor accessor, int action) {
      int birthday = accessor.readInt();
      int sn = accessor.readInt();
      String characterName = accessor.readMapleAsciiString();
      String message = accessor.readMapleAsciiString();
      return new SendGiftPacket(action, birthday, sn, characterName, message);
   }

   private BaseCashOperationPacket readMakePurchase(SeekableLittleEndianAccessor accessor, int action) {
      accessor.readByte();
      final int useNX = accessor.readInt();
      final int snCS = accessor.readInt();
      return new MakePurchasePacket(action, useNX, snCS);
   }
}
