package tools.packet.factory;

import java.util.List;

import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventoryType;
import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
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
      registry.setHandler(FredrickMessage.class, packet -> this.fredrickMessage((FredrickMessage) packet));
      registry.setHandler(GetFredrick.class, packet -> this.getFredrick((GetFredrick) packet));
      registry.setHandler(GetFredrickInfo.class, packet -> this.getFredrickInfo((GetFredrickInfo) packet));
   }

   protected byte[] fredrickMessage(FredrickMessage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FREDRICK_MESSAGE.getValue());
      mplew.write(packet.operation());
      return mplew.getPacket();
   }

   protected byte[] getFredrick(GetFredrick packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FREDRICK.getValue());
      mplew.write(packet.operation());

      switch (packet.operation()) {
         case 0x24:
            mplew.skip(8);
            break;
         default:
            mplew.write(0);
            break;
      }

      return mplew.getPacket();
   }

   protected byte[] getFredrickInfo(GetFredrickInfo packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FREDRICK.getValue());
      mplew.write(0x23);
      mplew.writeInt(9030000); // Fredrick
      mplew.writeInt(32272); //id
      mplew.skip(5);
      mplew.writeInt(packet.merchantNetMeso());
      mplew.write(0);
      List<Pair<Item, MapleInventoryType>> items = ItemFactory.MERCHANT.loadItems(packet.characterId(), false);
      mplew.write(items.size());

      for (Pair<Item, MapleInventoryType> item : items) {
         addItemInfo(mplew, item.getLeft(), true);
      }
      mplew.skip(3);
      return mplew.getPacket();
   }
}