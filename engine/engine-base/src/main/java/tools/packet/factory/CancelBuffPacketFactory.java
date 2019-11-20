package tools.packet.factory;

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
      Handler.handle(CancelForeignDebuff.class).decorate(this::cancelForeignDebuff).register(registry);
      Handler.handle(CancelForeignBuff.class).decorate(this::cancelForeignBuff).register(registry);
      Handler.handle(CancelBuff.class).decorate(this::cancelBuff).register(registry);
      Handler.handle(CancelDebuff.class).decorate(this::cancelDebuff).size(19).register(registry);
      Handler.handle(CancelForeignSlowDebuff.class).decorate(this::cancelForeignSlowDebuff).register(registry);
      Handler.handle(CancelForeignChairSkillEffect.class).decorate(this::cancelForeignChairSkillEffect).size(19).register(registry);
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