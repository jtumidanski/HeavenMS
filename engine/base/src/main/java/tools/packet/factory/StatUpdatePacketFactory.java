package tools.packet.factory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import client.MapleStat;
import client.inventory.MaplePet;
import constants.GameConstants;
import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.stat.EnableActions;
import tools.packet.PacketInput;
import tools.packet.stat.UpdatePetStats;
import tools.packet.stat.UpdatePlayerStats;

public class StatUpdatePacketFactory extends AbstractPacketFactory {
   public static final List<Pair<MapleStat, Integer>> EMPTY_STATUPDATE = Collections.emptyList();

   private static StatUpdatePacketFactory instance;

   public static StatUpdatePacketFactory getInstance() {
      if (instance == null) {
         instance = new StatUpdatePacketFactory();
      }
      return instance;
   }

   private StatUpdatePacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof UpdatePlayerStats) {
         return create(this::updatePlayerStats, packetInput);
      } else if (packetInput instanceof EnableActions) {
         return create(this::enableActions, packetInput);
      } else if (packetInput instanceof UpdatePetStats) {
         return create(this::petStatUpdate, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   /**
    * Gets an empty stat update.
    *
    * @return The empty stat update packet.
    */
   protected byte[] enableActions(EnableActions packet) {
      return updatePlayerStats(new UpdatePlayerStats(EMPTY_STATUPDATE, true, null));
   }

   /**
    * Gets an update for specified stats.
    *
    * @return The stat update packet.
    */
   protected byte[] updatePlayerStats(UpdatePlayerStats packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.STAT_CHANGED.getValue());
      mplew.write(packet.isEnableActions() ? 1 : 0);
      int updateMask = 0;
      for (Pair<MapleStat, Integer> statupdate : packet.getStatup()) {
         updateMask |= statupdate.getLeft().getValue();
      }
      List<Pair<MapleStat, Integer>> mystats = packet.getStatup();
      if (mystats.size() > 1) {
         mystats.sort(new Comparator<>() {
            @Override
            public int compare(Pair<MapleStat, Integer> o1, Pair<MapleStat, Integer> o2) {
               int val1 = o1.getLeft().getValue();
               int val2 = o2.getLeft().getValue();
               return (val1 < val2 ? -1 : (val1 == val2 ? 0 : 1));
            }
         });
      }
      mplew.writeInt(updateMask);
      for (Pair<MapleStat, Integer> statupdate : mystats) {
         if (statupdate.getLeft().getValue() >= 1) {
            if (statupdate.getLeft().getValue() == 0x1) {
               mplew.write(statupdate.getRight().byteValue());
            } else if (statupdate.getLeft().getValue() <= 0x4) {
               mplew.writeInt(statupdate.getRight());
            } else if (statupdate.getLeft().getValue() < 0x20) {
               mplew.write(statupdate.getRight().shortValue());
            } else if (statupdate.getLeft().getValue() == 0x8000) {
               if (GameConstants.hasSPTable(packet.getMapleCharacter().getJob())) {
                  addRemainingSkillInfo(mplew, packet.getMapleCharacter());
               } else {
                  mplew.writeShort(statupdate.getRight().shortValue());
               }
            } else if (statupdate.getLeft().getValue() < 0xFFFF) {
               mplew.writeShort(statupdate.getRight().shortValue());
            } else if (statupdate.getLeft().getValue() == 0x20000) {
               mplew.writeShort(statupdate.getRight().shortValue());
            } else {
               mplew.writeInt(statupdate.getRight());
            }
         }
      }
      return mplew.getPacket();
   }

   protected byte[] petStatUpdate(UpdatePetStats packet) {
      // this actually does nothing... packet structure and stats needs to be uncovered

      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.STAT_CHANGED.getValue());
      int mask = 0;
      mask |= MapleStat.PET.getValue();
      mplew.write(0);
      mplew.writeInt(mask);
      for (int i = 0; i < 3; i++) {
         MaplePet pet = packet.pets()[i];
         if (pet != null) {
            mplew.writeLong(pet.uniqueId());
         } else {
            mplew.writeLong(0);
         }
      }
      mplew.write(0);
      return mplew.getPacket();
   }
}