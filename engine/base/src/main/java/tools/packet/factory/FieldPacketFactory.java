package tools.packet.factory;

import java.util.Map;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
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
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof ShowBossHP) {
         return create(this::showBossHP, packetInput);
      } else if (packetInput instanceof CustomShowBossHP) {
         return create(this::customShowBossHP, packetInput);
      } else if (packetInput instanceof EnvironmentChange) {
         return create(this::environmentChange, packetInput);
      } else if (packetInput instanceof MapEffect) {
         return create(this::mapEffect, packetInput);
      } else if (packetInput instanceof MapSound) {
         return create(this::mapSound, packetInput);
      } else if (packetInput instanceof DojoAnimation) {
         return create(this::sendDojoAnimation, packetInput);
      } else if (packetInput instanceof TrembleEffect) {
         return create(this::trembleEffect, packetInput);
      } else if (packetInput instanceof EnvironmentMove) {
         return create(this::environmentMove, packetInput);
      } else if (packetInput instanceof EnvironmentMoveList) {
         return create(this::environmentMoveList, packetInput);
      } else if (packetInput instanceof BlowWeather) {
         return create(this::startMapEffect, packetInput);
      } else if (packetInput instanceof RemoveWeather) {
         return create(this::removeMapEffect, packetInput);
      } else if (packetInput instanceof ChangeBackgroundEffect) {
         return create(this::changeBackgroundEffect, packetInput);
      } else if (packetInput instanceof ForcedEquip) {
         return create(this::showForcedEquip, packetInput);
      } else if (packetInput instanceof ForcedStatReset) {
         return create(this::resetForcedStats, packetInput);
      } else if (packetInput instanceof ForcedStatSet) {
         return create(this::aranGodlyStats, packetInput);
      } else if (packetInput instanceof CrimsonBalrogBoat) {
         return create(this::crogBoatPacket, packetInput);
      } else if (packetInput instanceof Boat) {
         return create(this::boatPacket, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] showBossHP(ShowBossHP packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FIELD_EFFECT.getValue());
      mplew.write(5);
      mplew.writeInt(packet.objectId());
      mplew.writeInt(packet.currentHP());
      mplew.writeInt(packet.maximumHP());
      mplew.write(packet.tagColor());
      mplew.write(packet.tagBackgroundColor());
      return mplew.getPacket();
   }

   protected byte[] customShowBossHP(CustomShowBossHP packet) {
      Pair<Integer, Integer> customHP = normalizedCustomMaxHP(packet.currentHP(), packet.maximumHP());
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FIELD_EFFECT.getValue());
      mplew.write(packet.call());
      mplew.writeInt(packet.objectId());
      mplew.writeInt(customHP.left);
      mplew.writeInt(customHP.right);
      mplew.write(packet.tagColor());
      mplew.write(packet.tagBackgroundColor());
      return mplew.getPacket();
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

   protected byte[] environmentChange(EnvironmentChange packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FIELD_EFFECT.getValue());
      mplew.write(packet.mode());
      mplew.writeMapleAsciiString(packet.env());
      return mplew.getPacket();
   }

   protected byte[] mapEffect(MapEffect packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FIELD_EFFECT.getValue());
      mplew.write(3);
      mplew.writeMapleAsciiString(packet.path());
      return mplew.getPacket();
   }

   protected byte[] mapSound(MapSound packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FIELD_EFFECT.getValue());
      mplew.write(4);
      mplew.writeMapleAsciiString(packet.path());
      return mplew.getPacket();
   }

   protected byte[] sendDojoAnimation(DojoAnimation packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FIELD_EFFECT.getValue());
      mplew.write(packet.firstByte());
      mplew.writeMapleAsciiString(packet.animation());
      return mplew.getPacket();
   }

   protected byte[] trembleEffect(TrembleEffect packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FIELD_EFFECT.getValue());
      mplew.write(1);
      mplew.write(packet.theType());
      mplew.writeInt(packet.delay());
      return mplew.getPacket();
   }

   protected byte[] environmentMove(EnvironmentMove packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FIELD_OBSTACLE_ONOFF.getValue());
      mplew.writeMapleAsciiString(packet.environment());
      mplew.writeInt(packet.mode());   // 0: stop and back to start, 1: move
      return mplew.getPacket();
   }

   protected byte[] environmentMoveList(EnvironmentMoveList packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FIELD_OBSTACLE_ONOFF_LIST.getValue());
      mplew.writeInt(packet.getEnvironmentMoveList().size());
      for (Map.Entry<String, Integer> envMove : packet.getEnvironmentMoveList()) {
         mplew.writeMapleAsciiString(envMove.getKey());
         mplew.writeInt(envMove.getValue());
      }
      return mplew.getPacket();
   }

   protected byte[] environmentMoveReset() {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FIELD_OBSTACLE_ALL_RESET.getValue());
      return mplew.getPacket();
   }

   protected byte[] startMapEffect(BlowWeather packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.BLOW_WEATHER.getValue());
      mplew.write(packet.active() ? 0 : 1);
      mplew.writeInt(packet.itemId());
      if (packet.active()) {
         mplew.writeMapleAsciiString(packet.message());
      }
      return mplew.getPacket();
   }

   protected byte[] removeMapEffect(RemoveWeather packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.BLOW_WEATHER.getValue());
      mplew.write(0);
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   /**
    * Changes the current background effect to either being rendered or not.
    * Data is still missing, so this is pretty binary at the moment in how it
    * behaves.
    *
    * @return a packet to change the background effect of a specified layer.
    */
   protected byte[] changeBackgroundEffect(ChangeBackgroundEffect packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SET_BACK_EFFECT.getValue());
      mplew.writeBool(packet.remove());
      mplew.writeInt(0); // not sure what this int32 does yet
      mplew.write(packet.layer());
      mplew.writeInt(packet.transition());
      return mplew.getPacket();
   }

   protected byte[] showForcedEquip(ForcedEquip packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FORCED_MAP_EQUIP.getValue());
      if (packet.team() > -1) {
         mplew.write(packet.team());   // 00 = red, 01 = blue
      }
      return mplew.getPacket();
   }

   protected byte[] resetForcedStats(ForcedStatReset packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(2);
      mplew.writeShort(SendOpcode.FORCED_STAT_RESET.getValue());
      return mplew.getPacket();
   }

   protected byte[] aranGodlyStats(ForcedStatSet packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.FORCED_STAT_SET.getValue());
      mplew.write(new byte[]{(byte) 0x1F, (byte) 0x0F, 0, 0, (byte) 0xE7, 3, (byte) 0xE7, 3, (byte) 0xE7, 3, (byte) 0xE7, 3, (byte) 0xFF, 0, (byte) 0xE7, 3, (byte) 0xE7, 3, (byte) 0x78, (byte) 0x8C});
      return mplew.getPacket();
   }

   protected byte[] crogBoatPacket(CrimsonBalrogBoat packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CONTI_MOVE.getValue());
      mplew.write(10);
      mplew.write(packet.theType() ? 4 : 5);
      return mplew.getPacket();
   }

   protected byte[] boatPacket(Boat packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CONTI_STATE.getValue());
      mplew.write(packet.theType() ? 1 : 2);
      mplew.write(0);
      return mplew.getPacket();
   }
}