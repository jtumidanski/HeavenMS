package tools.packet.factory;

import client.inventory.Item;
import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.storage.ArrangeStorage;
import tools.packet.storage.GetStorage;
import tools.packet.storage.MesoStorage;
import tools.packet.storage.StorageError;
import tools.packet.storage.StoreInStorage;
import tools.packet.storage.TakeOutOfStorage;

public class StoragePacketFactory extends AbstractPacketFactory {
   private static StoragePacketFactory instance;

   public static StoragePacketFactory getInstance() {
      if (instance == null) {
         instance = new StoragePacketFactory();
      }
      return instance;
   }

   private StoragePacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof GetStorage) {
         return create(this::getStorage, packetInput);
      } else if (packetInput instanceof StorageError) {
         return create(this::getStorageError, packetInput);
      } else if (packetInput instanceof MesoStorage) {
         return create(this::mesoStorage, packetInput);
      } else if (packetInput instanceof StoreInStorage) {
         return create(this::storeStorage, packetInput);
      } else if (packetInput instanceof TakeOutOfStorage) {
         return create(this::takeOutStorage, packetInput);
      } else if (packetInput instanceof ArrangeStorage) {
         return create(this::arrangeStorage, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] getStorage(GetStorage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.STORAGE.getValue());
      mplew.write(0x16);
      mplew.writeInt(packet.npcId());
      mplew.write(packet.slots());
      mplew.writeShort(0x7E);
      mplew.writeShort(0);
      mplew.writeInt(0);
      mplew.writeInt(packet.meso());
      mplew.writeShort(0);
      mplew.write((byte) packet.items().size());
      packet.items().forEach(item -> addItemInfo(mplew, item, true));
      mplew.writeShort(0);
      mplew.write(0);
      return mplew.getPacket();
   }

   /*
    * 0x0A = Inv full
    * 0x0B = You do not have enough mesos
    * 0x0C = One-Of-A-Kind error
    */
   protected byte[] getStorageError(StorageError packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.STORAGE.getValue());
      mplew.write(packet.theType());
      return mplew.getPacket();
   }

   protected byte[] mesoStorage(MesoStorage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.STORAGE.getValue());
      mplew.write(0x13);
      mplew.write(packet.slots());
      mplew.writeShort(2);
      mplew.writeShort(0);
      mplew.writeInt(0);
      mplew.writeInt(packet.meso());
      return mplew.getPacket();
   }

   protected byte[] storeStorage(StoreInStorage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.STORAGE.getValue());
      mplew.write(0xD);
      mplew.write(packet.slots());
      mplew.writeShort(packet.inventoryType().getBitfieldEncoding());
      mplew.writeShort(0);
      mplew.writeInt(0);
      mplew.write(packet.items().size());
      for (Item item : packet.items()) {
         addItemInfo(mplew, item, true);
      }
      return mplew.getPacket();
   }

   protected byte[] takeOutStorage(TakeOutOfStorage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.STORAGE.getValue());
      mplew.write(0x9);
      mplew.write(packet.slots());
      mplew.writeShort(packet.inventoryType().getBitfieldEncoding());
      mplew.writeShort(0);
      mplew.writeInt(0);
      mplew.write(packet.items().size());
      for (Item item : packet.items()) {
         addItemInfo(mplew, item, true);
      }
      return mplew.getPacket();
   }

   protected byte[] arrangeStorage(ArrangeStorage packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.STORAGE.getValue());
      mplew.write(0xF);
      mplew.write(packet.slots());
      mplew.write(124);
      mplew.skip(10);
      mplew.write(packet.items().size());
      for (Item item : packet.items()) {
         addItemInfo(mplew, item, true);
      }
      mplew.write(0);
      return mplew.getPacket();
   }
}