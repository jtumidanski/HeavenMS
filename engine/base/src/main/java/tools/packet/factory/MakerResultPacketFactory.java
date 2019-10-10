package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.maker.MakerCrystalResult;
import tools.packet.maker.MakerEnableActions;
import tools.packet.maker.MakerResult;
import tools.packet.maker.MakerResultDesynth;

public class MakerResultPacketFactory extends AbstractPacketFactory {
   private static MakerResultPacketFactory instance;

   public static MakerResultPacketFactory getInstance() {
      if (instance == null) {
         instance = new MakerResultPacketFactory();
      }
      return instance;
   }

   private MakerResultPacketFactory() {
      registry.setHandler(MakerResult.class, packet -> this.makerResult((MakerResult) packet));
      registry.setHandler(MakerCrystalResult.class, packet -> this.makerResultCrystal((MakerCrystalResult) packet));
      registry.setHandler(MakerResultDesynth.class, packet -> this.makerResultDesynth((MakerResultDesynth) packet));
      registry.setHandler(MakerEnableActions.class, packet -> this.makerEnableActions((MakerEnableActions) packet));
   }

   // MAKER_RESULT packets thanks to Arnah (Vertisy)
   protected byte[] makerResult(MakerResult packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MAKER_RESULT.getValue());
      mplew.writeInt(packet.success() ? 0 : 1); // 0 = success, 1 = fail
      mplew.writeInt(1); // 1 or 2 doesn't matter, same methods
      mplew.writeBool(!packet.success());
      if (packet.success()) {
         mplew.writeInt(packet.itemMade());
         mplew.writeInt(packet.itemCount());
      }
      mplew.writeInt(packet.itemsLost().size()); // Loop
      for (Pair<Integer, Integer> item : packet.itemsLost()) {
         mplew.writeInt(item.getLeft());
         mplew.writeInt(item.getRight());
      }
      mplew.writeInt(packet.incBuffGems().size());
      for (Integer gem : packet.incBuffGems()) {
         mplew.writeInt(gem);
      }
      if (packet.catalystId() != -1) {
         mplew.write(1); // stimulator
         mplew.writeInt(packet.catalystId());
      } else {
         mplew.write(0);
      }

      mplew.writeInt(packet.mesos());
      return mplew.getPacket();
   }

   protected byte[] makerResultCrystal(MakerCrystalResult packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MAKER_RESULT.getValue());
      mplew.writeInt(0); // Always successful!
      mplew.writeInt(3); // Monster Crystal
      mplew.writeInt(packet.itemIdGained());
      mplew.writeInt(packet.itemIdLost());
      return mplew.getPacket();
   }

   protected byte[] makerResultDesynth(MakerResultDesynth packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MAKER_RESULT.getValue());
      mplew.writeInt(0); // Always successful!
      mplew.writeInt(4); // Mode Desynth
      mplew.writeInt(packet.itemId()); // Item desynthed
      mplew.writeInt(packet.itemsGained().size()); // Loop of items gained, (int, int)
      for (Pair<Integer, Integer> item : packet.itemsGained()) {
         mplew.writeInt(item.getLeft());
         mplew.writeInt(item.getRight());
      }
      mplew.writeInt(packet.mesos()); // Mesos spent.
      return mplew.getPacket();
   }

   protected byte[] makerEnableActions(MakerEnableActions packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MAKER_RESULT.getValue());
      mplew.writeInt(0); // Always successful!
      mplew.writeInt(0); // Monster Crystal
      mplew.writeInt(0);
      mplew.writeInt(0);
      return mplew.getPacket();
   }
}