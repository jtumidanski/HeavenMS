package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.quest.info.AddQuestTimeLimit;
import tools.packet.quest.info.QuestError;
import tools.packet.quest.info.QuestExpire;
import tools.packet.quest.info.QuestFailure;
import tools.packet.quest.info.QuestFinish;
import tools.packet.quest.info.RemoveQuestTimeLimit;
import tools.packet.quest.ShowQuestComplete;
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
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof UpdateQuestInfo) {
         return create(this::updateQuestInfo, packetInput);
      } else if (packetInput instanceof AddQuestTimeLimit) {
         return create(this::addQuestTimeLimit, packetInput);
      } else if (packetInput instanceof RemoveQuestTimeLimit) {
         return create(this::removeQuestTimeLimit, packetInput);
      } else if (packetInput instanceof QuestFinish) {
         return create(this::updateQuestFinish, packetInput);
      } else if (packetInput instanceof QuestError) {
         return create(this::questError, packetInput);
      } else if (packetInput instanceof QuestFailure) {
         return create(this::questFailure, packetInput);
      } else if (packetInput instanceof QuestExpire) {
         return create(this::questExpire, packetInput);
      } else if (packetInput instanceof ShowQuestComplete) {
         return create(this::getShowQuestCompletion, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] updateQuestInfo(UpdateQuestInfo packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.UPDATE_QUEST_INFO.getValue());
      mplew.write(8); //0x0A in v95
      mplew.writeShort(packet.questId());
      mplew.writeInt(packet.npcId());
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   protected byte[] addQuestTimeLimit(AddQuestTimeLimit packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.UPDATE_QUEST_INFO.getValue());
      mplew.write(6);
      mplew.writeShort(1);//Size but meh, when will there be 2 at the same time? And it won't even replace the old one :)
      mplew.writeShort(packet.questId());
      mplew.writeInt(packet.time());
      return mplew.getPacket();
   }

   protected byte[] removeQuestTimeLimit(RemoveQuestTimeLimit packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.UPDATE_QUEST_INFO.getValue());
      mplew.write(7);
      mplew.writeShort(1);//Position
      mplew.writeShort(packet.questId());
      return mplew.getPacket();
   }

   protected byte[] updateQuestFinish(QuestFinish packet) { //Check
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.UPDATE_QUEST_INFO.getValue()); //0xF2 in v95
      mplew.write(8);//0x0A in v95
      mplew.writeShort(packet.questId());
      mplew.writeInt(packet.npcId());
      mplew.writeShort(packet.nextQuestId());
      return mplew.getPacket();
   }

   protected byte[] questError(QuestError packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.UPDATE_QUEST_INFO.getValue());
      mplew.write(0x0A);
      mplew.writeShort(packet.questId());
      return mplew.getPacket();
   }

   protected byte[] questFailure(QuestFailure packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.UPDATE_QUEST_INFO.getValue());
      mplew.write(packet.theType());//0x0B = No meso, 0x0D = Worn by character, 0x0E = Not having the item ?
      return mplew.getPacket();
   }

   protected byte[] questExpire(QuestExpire packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.UPDATE_QUEST_INFO.getValue());
      mplew.write(0x0F);
      mplew.writeShort(packet.questId());
      return mplew.getPacket();
   }

   protected byte[] getShowQuestCompletion(ShowQuestComplete packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.QUEST_CLEAR.getValue());
      mplew.writeShort(packet.questId());
      return mplew.getPacket();
   }
}