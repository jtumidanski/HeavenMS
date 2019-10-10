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
      registry.setHandler(CancelForeignDebuff.class, packet -> create(SendOpcode.CANCEL_FOREIGN_BUFF, this::cancelForeignDebuff, packet));
      registry.setHandler(CancelForeignBuff.class, packet -> create(SendOpcode.CANCEL_FOREIGN_BUFF, this::cancelForeignBuff, packet));
      registry.setHandler(CancelBuff.class, packet -> create(SendOpcode.CANCEL_BUFF, this::cancelBuff, packet));
      registry.setHandler(CancelDebuff.class, packet -> create(SendOpcode.CANCEL_BUFF, this::cancelDebuff, packet, 19));
      registry.setHandler(CancelForeignSlowDebuff.class, packet -> create(SendOpcode.CANCEL_FOREIGN_BUFF, this::cancelForeignSlowDebuff, packet));
      registry.setHandler(CancelForeignChairSkillEffect.class, packet -> create(SendOpcode.CANCEL_FOREIGN_BUFF, this::cancelForeignChairSkillEffect, packet, 19));
   }

   protected void cancelForeignDebuff(MaplePacketLittleEndianWriter writer, CancelForeignDebuff packet) {
      writer.writeInt(packet.characterId());
      writer.writeLong(0);
      writer.writeLong(packet.mask());
   }

   protected void cancelForeignBuff(MaplePacketLittleEndianWriter writer, CancelForeignBuff packet) {
      writer.writeInt(packet.characterId());
      writeLongMaskFromList(writer, packet.statups());
   }

   protected void cancelBuff(MaplePacketLittleEndianWriter writer, CancelBuff packet) {
      writeLongMaskFromList(writer, packet.statups());
      writer.write(1);//?
   }

   protected void cancelDebuff(MaplePacketLittleEndianWriter writer, CancelDebuff packet) {
      writer.writeLong(0);
      writer.writeLong(packet.mask());
      writer.write(0);
   }

   protected void cancelForeignSlowDebuff(MaplePacketLittleEndianWriter writer, CancelForeignSlowDebuff packet) {
      writer.writeInt(packet.characterId());
      writeLongMaskSlowD(writer);
   }

   protected void cancelForeignChairSkillEffect(MaplePacketLittleEndianWriter writer, CancelForeignChairSkillEffect packet) {
      writer.writeInt(packet.characterId());
      writeLongMaskChair(writer);
   }
}