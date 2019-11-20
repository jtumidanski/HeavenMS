package tools.packet.factory;

import client.inventory.Item;
import tools.data.output.MaplePacketLittleEndianWriter;
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
      Handler.handle(GetStorage.class).decorate(this::getStorage).register(registry);
      Handler.handle(StorageError.class).decorate(this::getStorageError).register(registry);
      Handler.handle(MesoStorage.class).decorate(this::mesoStorage).register(registry);
      Handler.handle(StoreInStorage.class).decorate(this::storeStorage).register(registry);
      Handler.handle(TakeOutOfStorage.class).decorate(this::takeOutStorage).register(registry);
      Handler.handle(ArrangeStorage.class).decorate(this::arrangeStorage).register(registry);
   }

   protected void getStorage(MaplePacketLittleEndianWriter writer, GetStorage packet) {
      writer.write(0x16);
      writer.writeInt(packet.npcId());
      writer.write(packet.slots());
      writer.writeShort(0x7E);
      writer.writeShort(0);
      writer.writeInt(0);
      writer.writeInt(packet.meso());
      writer.writeShort(0);
      writer.write((byte) packet.items().size());
      packet.items().forEach(item -> addItemInfo(writer, item, true));
      writer.writeShort(0);
      writer.write(0);
   }

   /*
    * 0x0A = Inv full
    * 0x0B = You do not have enough mesos
    * 0x0C = One-Of-A-Kind error
    */
   protected void getStorageError(MaplePacketLittleEndianWriter writer, StorageError packet) {
      writer.write(packet.theType());
   }

   protected void mesoStorage(MaplePacketLittleEndianWriter writer, MesoStorage packet) {
      writer.write(0x13);
      writer.write(packet.slots());
      writer.writeShort(2);
      writer.writeShort(0);
      writer.writeInt(0);
      writer.writeInt(packet.meso());
   }

   protected void storeStorage(MaplePacketLittleEndianWriter writer, StoreInStorage packet) {
      writer.write(0xD);
      writer.write(packet.slots());
      writer.writeShort(packet.inventoryType().getBitfieldEncoding());
      writer.writeShort(0);
      writer.writeInt(0);
      writer.write(packet.items().size());
      for (Item item : packet.items()) {
         addItemInfo(writer, item, true);
      }
   }

   protected void takeOutStorage(MaplePacketLittleEndianWriter writer, TakeOutOfStorage packet) {
      writer.write(0x9);
      writer.write(packet.slots());
      writer.writeShort(packet.inventoryType().getBitfieldEncoding());
      writer.writeShort(0);
      writer.writeInt(0);
      writer.write(packet.items().size());
      for (Item item : packet.items()) {
         addItemInfo(writer, item, true);
      }
   }

   protected void arrangeStorage(MaplePacketLittleEndianWriter writer, ArrangeStorage packet) {
      writer.write(0xF);
      writer.write(packet.slots());
      writer.write(124);
      writer.skip(10);
      writer.write(packet.items().size());
      for (Item item : packet.items()) {
         addItemInfo(writer, item, true);
      }
      writer.write(0);
   }
}