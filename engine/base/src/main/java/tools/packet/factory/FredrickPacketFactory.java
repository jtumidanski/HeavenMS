package tools.packet.factory;

import java.util.List;

import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventoryType;
import net.opcodes.SendOpcode;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.fredrick.FredrickMessage;
import tools.packet.fredrick.GetFredrick;
import tools.packet.fredrick.GetFredrickInfo;

public class FredrickPacketFactory extends AbstractPacketFactory {
   private static FredrickPacketFactory instance;

   public static FredrickPacketFactory getInstance() {
      if (instance == null) {
         instance = new FredrickPacketFactory();
      }
      return instance;
   }

   private FredrickPacketFactory() {
      registry.setHandler(FredrickMessage.class, packet -> create(SendOpcode.FREDRICK_MESSAGE, this::fredrickMessage, packet));
      registry.setHandler(GetFredrick.class, packet -> create(SendOpcode.FREDRICK, this::getFredrick, packet));
      registry.setHandler(GetFredrickInfo.class, packet -> create(SendOpcode.FREDRICK, this::getFredrickInfo, packet));
   }

   protected void fredrickMessage(MaplePacketLittleEndianWriter writer, FredrickMessage packet) {
      writer.write(packet.operation());
   }

   protected void getFredrick(MaplePacketLittleEndianWriter writer, GetFredrick packet) {
      writer.write(packet.operation());
      if (packet.operation() == 0x24) {
         writer.skip(8);
      } else {
         writer.write(0);
      }
   }

   protected void getFredrickInfo(MaplePacketLittleEndianWriter writer, GetFredrickInfo packet) {
      writer.write(0x23);
      writer.writeInt(9030000); // Fredrick
      writer.writeInt(32272); //id
      writer.skip(5);
      writer.writeInt(packet.merchantNetMeso());
      writer.write(0);
      List<Pair<Item, MapleInventoryType>> items = ItemFactory.MERCHANT.loadItems(packet.characterId(), false);
      writer.write(items.size());

      for (Pair<Item, MapleInventoryType> item : items) {
         addItemInfo(writer, item.getLeft(), true);
      }
      writer.skip(3);
   }
}