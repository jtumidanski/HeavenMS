package tools.packet.factory;

import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.maker.MakerCrystalResult;
import tools.packet.maker.MakerEnableActions;
import tools.packet.maker.MakerResult;
import tools.packet.maker.MakerResultDestroy;

public class MakerResultPacketFactory extends AbstractPacketFactory {
   private static MakerResultPacketFactory instance;

   public static MakerResultPacketFactory getInstance() {
      if (instance == null) {
         instance = new MakerResultPacketFactory();
      }
      return instance;
   }

   private MakerResultPacketFactory() {
      Handler.handle(MakerResult.class).decorate(this::makerResult).register(registry);
      Handler.handle(MakerCrystalResult.class).decorate(this::makerResultCrystal).register(registry);
      Handler.handle(MakerResultDestroy.class).decorate(this::makerResultDestroy).register(registry);
      Handler.handle(MakerEnableActions.class).decorate(this::makerEnableActions).register(registry);
   }

   protected void makerResult(MaplePacketLittleEndianWriter writer, MakerResult packet) {
      writer.writeInt(packet.success() ? 0 : 1); // 0 = success, 1 = fail
      writer.writeInt(1); // 1 or 2 doesn't matter, same methods
      writer.writeBool(!packet.success());
      if (packet.success()) {
         writer.writeInt(packet.itemMade());
         writer.writeInt(packet.itemCount());
      }
      writer.writeInt(packet.itemsLost().size()); // Loop
      for (Pair<Integer, Integer> item : packet.itemsLost()) {
         writer.writeInt(item.getLeft());
         writer.writeInt(item.getRight());
      }
      writer.writeInt(packet.incBuffGems().size());
      for (Integer gem : packet.incBuffGems()) {
         writer.writeInt(gem);
      }
      if (packet.catalystId() != -1) {
         writer.write(1); // stimulator
         writer.writeInt(packet.catalystId());
      } else {
         writer.write(0);
      }

      writer.writeInt(packet.mesos());
   }

   protected void makerResultCrystal(MaplePacketLittleEndianWriter writer, MakerCrystalResult packet) {
      writer.writeInt(0); // Always successful!
      writer.writeInt(3); // Monster Crystal
      writer.writeInt(packet.itemIdGained());
      writer.writeInt(packet.itemIdLost());
   }

   protected void makerResultDestroy(MaplePacketLittleEndianWriter writer, MakerResultDestroy packet) {
      writer.writeInt(0); // Always successful!
      writer.writeInt(4);
      writer.writeInt(packet.itemId());
      writer.writeInt(packet.itemsGained().size()); // Loop of items gained, (int, int)
      for (Pair<Integer, Integer> item : packet.itemsGained()) {
         writer.writeInt(item.getLeft());
         writer.writeInt(item.getRight());
      }
      writer.writeInt(packet.mesos()); // Mesos spent.
   }

   protected void makerEnableActions(MaplePacketLittleEndianWriter writer, MakerEnableActions packet) {
      writer.writeInt(0); // Always successful!
      writer.writeInt(0); // Monster Crystal
      writer.writeInt(0);
      writer.writeInt(0);
   }
}