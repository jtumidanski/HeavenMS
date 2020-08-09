package tools.packet.factory;

import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.quest.info.ShowQuestComplete;
import tools.packet.quest.info.AddQuestTimeLimit;
import tools.packet.quest.info.QuestError;
import tools.packet.quest.info.QuestExpire;
import tools.packet.quest.info.QuestFailure;
import tools.packet.quest.info.QuestFinish;
import tools.packet.quest.info.RemoveQuestTimeLimit;
import tools.packet.quest.UpdateQuestInfo;

public class QuestPacketFactory extends AbstractPacketFactory {
   private static QuestPacketFactory instance;

   public static QuestPacketFactory getInstance() {
      if (instance == null) {
         instance = new QuestPacketFactory();
      }
      return instance;
   }

   private QuestPacketFactory() {
      Handler.handle(UpdateQuestInfo.class).decorate(this::updateQuestInfo).register(registry);
      Handler.handle(AddQuestTimeLimit.class).decorate(this::addQuestTimeLimit).register(registry);
      Handler.handle(RemoveQuestTimeLimit.class).decorate(this::removeQuestTimeLimit).register(registry);
      Handler.handle(QuestFinish.class).decorate(this::updateQuestFinish).register(registry);
      Handler.handle(QuestError.class).decorate(this::questError).register(registry);
      Handler.handle(QuestFailure.class).decorate(this::questFailure).register(registry);
      Handler.handle(QuestExpire.class).decorate(this::questExpire).register(registry);
      Handler.handle(ShowQuestComplete.class).decorate(this::getShowQuestCompletion).register(registry);
   }

   protected void updateQuestInfo(MaplePacketLittleEndianWriter writer, UpdateQuestInfo packet) {
      writer.write(8); //0x0A in v95
      writer.writeShort(packet.questId());
      writer.writeInt(packet.npcId());
      writer.writeInt(0);
   }

   protected void addQuestTimeLimit(MaplePacketLittleEndianWriter writer, AddQuestTimeLimit packet) {
      writer.write(6);
      writer.writeShort(1);//Size but meh, when will there be 2 at the same time? And it won't even replace the old one :)
      writer.writeShort(packet.questId());
      writer.writeInt(packet.time());
   }

   protected void removeQuestTimeLimit(MaplePacketLittleEndianWriter writer, RemoveQuestTimeLimit packet) {
      writer.write(7);
      writer.writeShort(1);//Position
      writer.writeShort(packet.questId());
   }

   protected void updateQuestFinish(MaplePacketLittleEndianWriter writer, QuestFinish packet) { //Check
      writer.write(8);//0x0A in v95
      writer.writeShort(packet.questId());
      writer.writeInt(packet.npcId());
      writer.writeShort(packet.nextQuestId());
   }

   protected void questError(MaplePacketLittleEndianWriter writer, QuestError packet) {
      writer.write(0x0A);
      writer.writeShort(packet.questId());
   }

   protected void questFailure(MaplePacketLittleEndianWriter writer, QuestFailure packet) {
      writer.write(packet.theType());//0x0B = No meso, 0x0D = Worn by character, 0x0E = Not having the item ?
   }

   protected void questExpire(MaplePacketLittleEndianWriter writer, QuestExpire packet) {
      writer.write(0x0F);
      writer.writeShort(packet.questId());
   }

   protected void getShowQuestCompletion(MaplePacketLittleEndianWriter writer, ShowQuestComplete packet) {
      writer.writeShort(packet.questId());
   }
}