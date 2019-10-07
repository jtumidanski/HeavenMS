package tools.packet.factory;

import java.util.List;

import client.MapleBuffStat;
import client.MapleDisease;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;

public abstract class AbstractBuffPacketFactory extends AbstractPacketFactory {
   protected void writeLongMaskD(final MaplePacketLittleEndianWriter mplew, List<Pair<MapleDisease, Integer>> statups) {
      long firstmask = 0;
      long secondmask = 0;
      for (Pair<MapleDisease, Integer> statup : statups) {
         if (statup.getLeft().isFirst()) {
            firstmask |= statup.getLeft().getValue();
         } else {
            secondmask |= statup.getLeft().getValue();
         }
      }
      mplew.writeLong(firstmask);
      mplew.writeLong(secondmask);
   }

   protected void writeLongMask(final MaplePacketLittleEndianWriter mplew, List<Pair<MapleBuffStat, Integer>> statups) {
      long firstmask = 0;
      long secondmask = 0;
      for (Pair<MapleBuffStat, Integer> statup : statups) {
         if (statup.getLeft().isFirst()) {
            firstmask |= statup.getLeft().getValue();
         } else {
            secondmask |= statup.getLeft().getValue();
         }
      }
      mplew.writeLong(firstmask);
      mplew.writeLong(secondmask);
   }

   protected void writeLongMaskSlowD(final MaplePacketLittleEndianWriter mplew) {
      mplew.writeInt(0);
      mplew.writeInt(2048);
      mplew.writeLong(0);
   }

   protected void writeLongMaskChair(final MaplePacketLittleEndianWriter mplew) {
      mplew.writeInt(0);
      mplew.writeInt(262144);
      mplew.writeLong(0);
   }

   protected void writeLongMaskFromList(final MaplePacketLittleEndianWriter mplew, List<MapleBuffStat> statups) {
      long firstmask = 0;
      long secondmask = 0;
      for (MapleBuffStat statup : statups) {
         if (statup.isFirst()) {
            firstmask |= statup.getValue();
         } else {
            secondmask |= statup.getValue();
         }
      }
      mplew.writeLong(firstmask);
      mplew.writeLong(secondmask);
   }
}
