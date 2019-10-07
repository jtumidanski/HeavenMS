package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.buff.CancelBuff;
import tools.packet.buff.CancelDebuff;
import tools.packet.buff.CancelForeignBuff;
import tools.packet.buff.CancelForeignChairSkillEffect;
import tools.packet.buff.CancelForeignDebuff;
import tools.packet.buff.CancelForeignSlowDebuff;

public class CancelBuffPacketFactory extends AbstractBuffPacketFactory {
   private static CancelBuffPacketFactory instance;

   public static CancelBuffPacketFactory getInstance() {
      if (instance == null) {
         instance = new CancelBuffPacketFactory();
      }
      return instance;
   }

   private CancelBuffPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof CancelForeignDebuff) {
         return create(this::cancelForeignDebuff, packetInput);
      } else if (packetInput instanceof CancelForeignBuff) {
         return create(this::cancelForeignBuff, packetInput);
      } else if (packetInput instanceof CancelBuff) {
         return create(this::cancelBuff, packetInput);
      } else if (packetInput instanceof CancelDebuff) {
         return create(this::cancelDebuff, packetInput);
      } else if (packetInput instanceof CancelForeignSlowDebuff) {
         return create(this::cancelForeignSlowDebuff, packetInput);
      } else if (packetInput instanceof CancelForeignChairSkillEffect) {
         return create(this::cancelForeignChairSkillEffect, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] cancelForeignDebuff(CancelForeignDebuff packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CANCEL_FOREIGN_BUFF.getValue());
      mplew.writeInt(packet.characterId());
      mplew.writeLong(0);
      mplew.writeLong(packet.mask());
      return mplew.getPacket();
   }

   protected byte[] cancelForeignBuff(CancelForeignBuff packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CANCEL_FOREIGN_BUFF.getValue());
      mplew.writeInt(packet.characterId());
      writeLongMaskFromList(mplew, packet.statups());
      return mplew.getPacket();
   }

   protected byte[] cancelBuff(CancelBuff packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CANCEL_BUFF.getValue());
      writeLongMaskFromList(mplew, packet.statups());
      mplew.write(1);//?
      return mplew.getPacket();
   }

   protected byte[] cancelDebuff(CancelDebuff packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(19);
      mplew.writeShort(SendOpcode.CANCEL_BUFF.getValue());
      mplew.writeLong(0);
      mplew.writeLong(packet.mask());
      mplew.write(0);
      return mplew.getPacket();
   }

   protected byte[] cancelForeignSlowDebuff(CancelForeignSlowDebuff packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.CANCEL_FOREIGN_BUFF.getValue());
      mplew.writeInt(packet.characterId());
      writeLongMaskSlowD(mplew);
      return mplew.getPacket();
   }

   protected byte[] cancelForeignChairSkillEffect(CancelForeignChairSkillEffect packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(19);
      mplew.writeShort(SendOpcode.CANCEL_FOREIGN_BUFF.getValue());
      mplew.writeInt(packet.characterId());
      writeLongMaskChair(mplew);
      return mplew.getPacket();
   }
}