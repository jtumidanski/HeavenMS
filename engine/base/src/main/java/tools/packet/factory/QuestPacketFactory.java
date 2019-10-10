package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.quest.ShowQuestComplete;
import tools.packet.quest.info.AddQuestTimeLimit;
import tools.packet.quest.info.QuestError;
import tools.packet.quest.info.QuestExpire;
import tools.packet.quest.info.QuestFailure;
import tools.packet.quest.info.QuestFinish;
import tools.packet.quest.info.RemoveQuestTimeLimit;
import tools.packet.quest.info.UpdateQuestInfo;

public class QuestPacketFactory extends AbstractPacketFactory {
   private static QuestPacketFactory instance;

   public static QuestPacketFactory getInstance() {
      if (instance == null) {
         instance = new QuestPacketFactory();
      }
      return instance;
   }

   private QuestPacketFactory() {
      registry.setHandler(UpdateQuestInfo.class, packet -> create(SendOpcode.UPDATE_QUEST_INFO, this::updateQuestInfo, packet));
      registry.setHandler(AddQuestTimeLimit.class, packet -> create(SendOpcode.UPDATE_QUEST_INFO, this::addQuestTimeLimit, packet));
      registry.setHandler(RemoveQuestTimeLimit.class, packet -> create(SendOpcode.UPDATE_QUEST_INFO, this::removeQuestTimeLimit, packet));
      registry.setHandler(QuestFinish.class, packet -> create(SendOpcode.UPDATE_QUEST_INFO, this::updateQuestFinish, packet));
      registry.setHandler(QuestError.class, packet -> create(SendOpcode.UPDATE_QUEST_INFO, this::questError, packet));
      registry.setHandler(QuestFailure.class, packet -> create(SendOpcode.UPDATE_QUEST_INFO, this::questFailure, packet));
      registry.setHandler(QuestExpire.class, packet -> create(SendOpcode.UPDATE_QUEST_INFO, this::questExpire, packet));
      registry.setHandler(ShowQuestComplete.class, packet -> create(SendOpcode.QUEST_CLEAR, this::getShowQuestCompletion, packet));
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