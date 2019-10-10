package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
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
      registry.setHandler(CancelForeignDebuff.class, packet -> this.cancelForeignDebuff((CancelForeignDebuff) packet));
      registry.setHandler(CancelForeignBuff.class, packet -> this.cancelForeignBuff((CancelForeignBuff) packet));
      registry.setHandler(CancelBuff.class, packet -> this.cancelBuff((CancelBuff) packet));
      registry.setHandler(CancelDebuff.class, packet -> this.cancelDebuff((CancelDebuff) packet));
      registry.setHandler(CancelForeignSlowDebuff.class, packet -> this.cancelForeignSlowDebuff((CancelForeignSlowDebuff) packet));
      registry.setHandler(CancelForeignChairSkillEffect.class, packet -> this.cancelForeignChairSkillEffect((CancelForeignChairSkillEffect) packet));
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