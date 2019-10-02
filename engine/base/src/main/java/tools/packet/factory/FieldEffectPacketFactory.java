package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.field.effect.CustomShowBossHP;
import tools.packet.field.effect.DojoAnimation;
import tools.packet.field.effect.EnvironmentChange;
import tools.packet.field.effect.MapEffect;
import tools.packet.field.effect.MapSound;
import tools.packet.field.effect.ShowBossHP;
import tools.packet.field.effect.TrembleEffect;

public class FieldEffectPacketFactory extends AbstractPacketFactory {
   private static FieldEffectPacketFactory instance;

   public static FieldEffectPacketFactory getInstance() {
      if (instance == null) {
         instance = new FieldEffectPacketFactory();
      }
      return instance;
   }

   private FieldEffectPacketFactory() {
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
}