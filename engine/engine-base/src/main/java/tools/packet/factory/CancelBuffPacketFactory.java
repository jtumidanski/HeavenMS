package tools.packet.factory;

import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.buff.CancelBuff;
import tools.packet.buff.CancelAbnormalStatus;
import tools.packet.buff.CancelForeignBuff;
import tools.packet.buff.CancelForeignChairSkillEffect;
import tools.packet.buff.CancelForeignAbnormalStatus;
import tools.packet.buff.CancelForeignAbnormalStatusSlow;

public class CancelBuffPacketFactory extends AbstractBuffPacketFactory {
   private static CancelBuffPacketFactory instance;

   public static CancelBuffPacketFactory getInstance() {
      if (instance == null) {
         instance = new CancelBuffPacketFactory();
      }
      return instance;
   }

   private CancelBuffPacketFactory() {
      Handler.handle(CancelForeignAbnormalStatus.class).decorate(this::cancelForeignAbnormalStatus).register(registry);
      Handler.handle(CancelForeignBuff.class).decorate(this::cancelForeignBuff).register(registry);
      Handler.handle(CancelBuff.class).decorate(this::cancelBuff).register(registry);
      Handler.handle(CancelAbnormalStatus.class).decorate(this::cancelAbnormalStatus).size(19).register(registry);
      Handler.handle(CancelForeignAbnormalStatusSlow.class).decorate(this::cancelForeignAbnormalStatusSlow).register(registry);
      Handler.handle(CancelForeignChairSkillEffect.class).decorate(this::cancelForeignChairSkillEffect).size(19).register(registry);
   }

   protected void cancelForeignAbnormalStatus(MaplePacketLittleEndianWriter writer, CancelForeignAbnormalStatus packet) {
      writer.writeInt(packet.characterId());
      writer.writeLong(0);
      writer.writeLong(packet.mask());
   }

   protected void cancelForeignBuff(MaplePacketLittleEndianWriter writer, CancelForeignBuff packet) {
      writer.writeInt(packet.characterId());
      writeLongMaskFromList(writer, packet.statIncreases());
   }

   protected void cancelBuff(MaplePacketLittleEndianWriter writer, CancelBuff packet) {
      writeLongMaskFromList(writer, packet.statIncreases());
      writer.write(1);//?
   }

   protected void cancelAbnormalStatus(MaplePacketLittleEndianWriter writer, CancelAbnormalStatus packet) {
      writer.writeLong(0);
      writer.writeLong(packet.mask());
      writer.write(0);
   }

   protected void cancelForeignAbnormalStatusSlow(MaplePacketLittleEndianWriter writer, CancelForeignAbnormalStatusSlow packet) {
      writer.writeInt(packet.characterId());
      writeLongMaskSlowD(writer);
   }

   protected void cancelForeignChairSkillEffect(MaplePacketLittleEndianWriter writer, CancelForeignChairSkillEffect packet) {
      writer.writeInt(packet.characterId());
      writeLongMaskChair(writer);
   }
}