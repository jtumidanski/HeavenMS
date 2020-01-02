package tools.packet.factory;

import java.util.Collections;
import java.util.List;

import client.MapleStat;
import client.inventory.MaplePet;
import constants.game.GameConstants;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.stat.EnableActions;
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
      Handler.handle(UpdatePlayerStats.class).decorate(this::updatePlayerStats).register(registry);
      Handler.handle(EnableActions.class).decorate(this::enableActions).register(registry);
      Handler.handle(UpdatePetStats.class).decorate(this::petStatUpdate).register(registry);
   }

   /**
    * Gets an empty stat update.
    */
   protected void enableActions(MaplePacketLittleEndianWriter writer, EnableActions packet) {
      updatePlayerStats(writer, new UpdatePlayerStats(EMPTY_STATUPDATE, true, null));
   }

   /**
    * Gets an update for specified stats.
    */
   protected void updatePlayerStats(MaplePacketLittleEndianWriter writer, UpdatePlayerStats packet) {
      writer.write(packet.isEnableActions() ? 1 : 0);
      int updateMask = 0;
      for (Pair<MapleStat, Integer> statupdate : packet.getStatup()) {
         updateMask |= statupdate.getLeft().getValue();
      }
      List<Pair<MapleStat, Integer>> mystats = packet.getStatup();
      if (mystats.size() > 1) {
         mystats.sort((o1, o2) -> {
            int val1 = o1.getLeft().getValue();
            int val2 = o2.getLeft().getValue();
            return (Integer.compare(val1, val2));
         });
      }
      writer.writeInt(updateMask);
      for (Pair<MapleStat, Integer> statupdate : mystats) {
         if (statupdate.getLeft().getValue() >= 1) {
            if (statupdate.getLeft().getValue() == 0x1) {
               writer.write(statupdate.getRight().byteValue());
            } else if (statupdate.getLeft().getValue() <= 0x4) {
               writer.writeInt(statupdate.getRight());
            } else if (statupdate.getLeft().getValue() < 0x20) {
               writer.write(statupdate.getRight().shortValue());
            } else if (statupdate.getLeft().getValue() == 0x8000) {
               if (GameConstants.hasSPTable(packet.getMapleCharacter().getJob())) {
                  addRemainingSkillInfo(writer, packet.getMapleCharacter());
               } else {
                  writer.writeShort(statupdate.getRight().shortValue());
               }
            } else if (statupdate.getLeft().getValue() < 0xFFFF) {
               writer.writeShort(statupdate.getRight().shortValue());
            } else if (statupdate.getLeft().getValue() == 0x20000) {
               writer.writeShort(statupdate.getRight().shortValue());
            } else {
               writer.writeInt(statupdate.getRight());
            }
         }
      }
   }

   protected void petStatUpdate(MaplePacketLittleEndianWriter writer, UpdatePetStats packet) {
      // this actually does nothing... packet structure and stats needs to be uncovered
      int mask = 0;
      mask |= MapleStat.PET.getValue();
      writer.write(0);
      writer.writeInt(mask);
      for (int i = 0; i < 3; i++) {
         MaplePet pet = packet.pets()[i];
         if (pet != null) {
            writer.writeLong(pet.uniqueId());
         } else {
            writer.writeLong(0);
         }
      }
      writer.write(0);
   }
}