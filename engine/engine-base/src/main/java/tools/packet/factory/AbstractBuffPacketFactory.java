package tools.packet.factory;

import java.util.List;

import client.MapleBuffStat;
import client.MapleAbnormalStatus;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;

public abstract class AbstractBuffPacketFactory extends AbstractPacketFactory {
   protected void writeLongMaskD(final MaplePacketLittleEndianWriter writer, List<Pair<MapleAbnormalStatus, Integer>> statIncreases) {
      long firstMask = 0;
      long secondMask = 0;
      for (Pair<MapleAbnormalStatus, Integer> statIncrease : statIncreases) {
         if (statIncrease.getLeft().isFirst()) {
            firstMask |= statIncrease.getLeft().getValue();
         } else {
            secondMask |= statIncrease.getLeft().getValue();
         }
      }
      writer.writeLong(firstMask);
      writer.writeLong(secondMask);
   }

   protected void writeLongMask(final MaplePacketLittleEndianWriter writer, List<Pair<MapleBuffStat, Integer>> statups) {
      long firstMask = 0;
      long secondMask = 0;
      for (Pair<MapleBuffStat, Integer> statup : statups) {
         if (statup.getLeft().isFirst()) {
            firstMask |= statup.getLeft().getValue();
         } else {
            secondMask |= statup.getLeft().getValue();
         }
      }
      writer.writeLong(firstMask);
      writer.writeLong(secondMask);
   }

   protected void writeLongMaskSlowD(final MaplePacketLittleEndianWriter writer) {
      writer.writeInt(0);
      writer.writeInt(2048);
      writer.writeLong(0);
   }

   protected void writeLongMaskChair(final MaplePacketLittleEndianWriter writer) {
      writer.writeInt(0);
      writer.writeInt(262144);
      writer.writeLong(0);
   }

   protected void writeLongMaskFromList(final MaplePacketLittleEndianWriter writer, List<MapleBuffStat> statups) {
      long firstMask = 0;
      long secondMask = 0;
      for (MapleBuffStat statup : statups) {
         if (statup.isFirst()) {
            firstMask |= statup.getValue();
         } else {
            secondMask |= statup.getValue();
         }
      }
      writer.writeLong(firstMask);
      writer.writeLong(secondMask);
   }
}
