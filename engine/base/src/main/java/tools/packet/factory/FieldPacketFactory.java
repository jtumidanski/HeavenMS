package tools.packet.factory;

import java.util.Map;

import net.opcodes.SendOpcode;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.field.Boat;
import tools.packet.field.CrimsonBalrogBoat;
import tools.packet.field.effect.BlowWeather;
import tools.packet.field.effect.ChangeBackgroundEffect;
import tools.packet.field.effect.CustomShowBossHP;
import tools.packet.field.effect.DojoAnimation;
import tools.packet.field.effect.EnvironmentChange;
import tools.packet.field.effect.ForcedEquip;
import tools.packet.field.effect.ForcedStatReset;
import tools.packet.field.effect.ForcedStatSet;
import tools.packet.field.effect.MapEffect;
import tools.packet.field.effect.MapSound;
import tools.packet.field.effect.RemoveWeather;
import tools.packet.field.effect.ShowBossHP;
import tools.packet.field.effect.TrembleEffect;
import tools.packet.field.obstacle.EnvironmentMove;
import tools.packet.field.obstacle.EnvironmentMoveList;

public class FieldPacketFactory extends AbstractPacketFactory {
   private static FieldPacketFactory instance;

   public static FieldPacketFactory getInstance() {
      if (instance == null) {
         instance = new FieldPacketFactory();
      }
      return instance;
   }

   private FieldPacketFactory() {
      registry.setHandler(ShowBossHP.class, packet -> create(SendOpcode.FIELD_EFFECT, this::showBossHP, packet));
      registry.setHandler(CustomShowBossHP.class, packet -> create(SendOpcode.FIELD_EFFECT, this::customShowBossHP, packet));
      registry.setHandler(EnvironmentChange.class, packet -> create(SendOpcode.FIELD_EFFECT, this::environmentChange, packet));
      registry.setHandler(MapEffect.class, packet -> create(SendOpcode.FIELD_EFFECT, this::mapEffect, packet));
      registry.setHandler(MapSound.class, packet -> create(SendOpcode.FIELD_EFFECT, this::mapSound, packet));
      registry.setHandler(DojoAnimation.class, packet -> create(SendOpcode.FIELD_EFFECT, this::sendDojoAnimation, packet));
      registry.setHandler(TrembleEffect.class, packet -> create(SendOpcode.FIELD_EFFECT, this::trembleEffect, packet));
      registry.setHandler(EnvironmentMove.class, packet -> create(SendOpcode.FIELD_OBSTACLE_ONOFF, this::environmentMove, packet));
      registry.setHandler(EnvironmentMoveList.class, packet -> create(SendOpcode.FIELD_OBSTACLE_ONOFF_LIST, this::environmentMoveList, packet));
      registry.setHandler(BlowWeather.class, packet -> create(SendOpcode.BLOW_WEATHER, this::startMapEffect, packet));
      registry.setHandler(RemoveWeather.class, packet -> create(SendOpcode.BLOW_WEATHER, this::removeMapEffect, packet));
      registry.setHandler(ChangeBackgroundEffect.class, packet -> create(SendOpcode.SET_BACK_EFFECT, this::changeBackgroundEffect, packet));
      registry.setHandler(ForcedEquip.class, packet -> create(SendOpcode.FORCED_MAP_EQUIP, this::showForcedEquip, packet));
      registry.setHandler(ForcedStatReset.class, packet -> create(SendOpcode.FORCED_STAT_RESET, this::resetForcedStats, packet, 2));
      registry.setHandler(ForcedStatSet.class, packet -> create(SendOpcode.FORCED_STAT_SET, this::aranGodlyStats, packet));
      registry.setHandler(CrimsonBalrogBoat.class, packet -> create(SendOpcode.CONTI_MOVE, this::crogBoatPacket, packet));
      registry.setHandler(Boat.class, packet -> create(SendOpcode.CONTI_STATE, this::boatPacket, packet));
   }

   protected void showBossHP(MaplePacketLittleEndianWriter writer, ShowBossHP packet) {
      writer.write(5);
      writer.writeInt(packet.objectId());
      writer.writeInt(packet.currentHP());
      writer.writeInt(packet.maximumHP());
      writer.write(packet.tagColor());
      writer.write(packet.tagBackgroundColor());
   }

   protected void customShowBossHP(MaplePacketLittleEndianWriter writer, CustomShowBossHP packet) {
      Pair<Integer, Integer> customHP = normalizedCustomMaxHP(packet.currentHP(), packet.maximumHP());
      writer.write(packet.call());
      writer.writeInt(packet.objectId());
      writer.writeInt(customHP.left);
      writer.writeInt(customHP.right);
      writer.write(packet.tagColor());
      writer.write(packet.tagBackgroundColor());
   }

   protected Pair<Integer, Integer> normalizedCustomMaxHP(long currHP, long maxHP) {
      int sendHP, sendMaxHP;
      if (maxHP <= Integer.MAX_VALUE) {
         sendHP = (int) currHP;
         sendMaxHP = (int) maxHP;
      } else {
         float f = ((float) currHP) / maxHP;
         sendHP = (int) (Integer.MAX_VALUE * f);
         sendMaxHP = Integer.MAX_VALUE;
      }
      return new Pair<>(sendHP, sendMaxHP);
   }

   protected void environmentChange(MaplePacketLittleEndianWriter writer, EnvironmentChange packet) {
      writer.write(packet.mode());
      writer.writeMapleAsciiString(packet.env());
   }

   protected void mapEffect(MaplePacketLittleEndianWriter writer, MapEffect packet) {
      writer.write(3);
      writer.writeMapleAsciiString(packet.path());
   }

   protected void mapSound(MaplePacketLittleEndianWriter writer, MapSound packet) {
      writer.write(4);
      writer.writeMapleAsciiString(packet.path());
   }

   protected void sendDojoAnimation(MaplePacketLittleEndianWriter writer, DojoAnimation packet) {
      writer.write(packet.firstByte());
      writer.writeMapleAsciiString(packet.animation());
   }

   protected void trembleEffect(MaplePacketLittleEndianWriter writer, TrembleEffect packet) {
      writer.write(1);
      writer.write(packet.theType());
      writer.writeInt(packet.delay());
   }

   protected void environmentMove(MaplePacketLittleEndianWriter writer, EnvironmentMove packet) {
      writer.writeMapleAsciiString(packet.environment());
      writer.writeInt(packet.mode());   // 0: stop and back to start, 1: move
   }

   protected void environmentMoveList(MaplePacketLittleEndianWriter writer, EnvironmentMoveList packet) {
      writer.writeInt(packet.getEnvironmentMoveList().size());
      for (Map.Entry<String, Integer> envMove : packet.getEnvironmentMoveList()) {
         writer.writeMapleAsciiString(envMove.getKey());
         writer.writeInt(envMove.getValue());
      }
   }

   protected void environmentMoveReset(MaplePacketLittleEndianWriter writer) {
   }

   protected void startMapEffect(MaplePacketLittleEndianWriter writer, BlowWeather packet) {
      writer.write(packet.active() ? 0 : 1);
      writer.writeInt(packet.itemId());
      if (packet.active()) {
         writer.writeMapleAsciiString(packet.message());
      }
   }

   protected void removeMapEffect(MaplePacketLittleEndianWriter writer, RemoveWeather packet) {
      writer.write(0);
      writer.writeInt(0);
   }

   /**
    * Changes the current background effect to either being rendered or not.
    * Data is still missing, so this is pretty binary at the moment in how it
    * behaves.
    *
    * @return a packet to change the background effect of a specified layer.
    */
   protected void changeBackgroundEffect(MaplePacketLittleEndianWriter writer, ChangeBackgroundEffect packet) {
      writer.writeBool(packet.remove());
      writer.writeInt(0); // not sure what this int32 does yet
      writer.write(packet.layer());
      writer.writeInt(packet.transition());
   }

   protected void showForcedEquip(MaplePacketLittleEndianWriter writer, ForcedEquip packet) {
      if (packet.team() > -1) {
         writer.write(packet.team());   // 00 = red, 01 = blue
      }
   }

   protected void resetForcedStats(MaplePacketLittleEndianWriter writer, ForcedStatReset packet) {
   }

   protected void aranGodlyStats(MaplePacketLittleEndianWriter writer, ForcedStatSet packet) {
      writer.write(new byte[]{(byte) 0x1F, (byte) 0x0F, 0, 0, (byte) 0xE7, 3, (byte) 0xE7, 3, (byte) 0xE7, 3, (byte) 0xE7, 3, (byte) 0xFF, 0, (byte) 0xE7, 3, (byte) 0xE7, 3, (byte) 0x78, (byte) 0x8C});
   }

   protected void crogBoatPacket(MaplePacketLittleEndianWriter writer, CrimsonBalrogBoat packet) {
      writer.write(10);
      writer.write(packet.theType() ? 4 : 5);
   }

   protected void boatPacket(MaplePacketLittleEndianWriter writer, Boat packet) {
      writer.write(packet.theType() ? 1 : 2);
      writer.write(0);
   }
}