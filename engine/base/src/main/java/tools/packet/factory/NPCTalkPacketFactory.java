package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.HexTool;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.npctalk.AskQuiz;
import tools.packet.npctalk.AskSpeedQuiz;
import tools.packet.npctalk.GetDimensionalMirror;
import tools.packet.npctalk.GetNPCTalk;
import tools.packet.npctalk.GetNPCTalkNum;
import tools.packet.npctalk.GetNPCTalkStyle;
import tools.packet.npctalk.GetNPCTalkText;

public class NPCTalkPacketFactory extends AbstractPacketFactory {
   private static NPCTalkPacketFactory instance;

   public static NPCTalkPacketFactory getInstance() {
      if (instance == null) {
         instance = new NPCTalkPacketFactory();
      }
      return instance;
   }

   private NPCTalkPacketFactory() {
      registry.setHandler(GetNPCTalk.class, packet -> this.getNPCTalk((GetNPCTalk) packet));
      registry.setHandler(GetDimensionalMirror.class, packet -> this.getDimensionalMirror((GetDimensionalMirror) packet));
      registry.setHandler(GetNPCTalkStyle.class, packet -> this.getNPCTalkStyle((GetNPCTalkStyle) packet));
      registry.setHandler(GetNPCTalkNum.class, packet -> this.getNPCTalkNum((GetNPCTalkNum) packet));
      registry.setHandler(GetNPCTalkText.class, packet -> this.getNPCTalkText((GetNPCTalkText) packet));
      registry.setHandler(AskQuiz.class, packet -> this.onAskQuiz((AskQuiz) packet));
      registry.setHandler(AskSpeedQuiz.class, packet -> this.onAskSpeedQuiz((AskSpeedQuiz) packet));
   }

   /**
    * Possible values for <code>speaker</code>:<br> 0: Npc talking (left)<br>
    * 1: Npc talking (right)<br> 2: Player talking (left)<br> 3: Player talking
    * (left)<br>
    */
   protected byte[] getNPCTalk(GetNPCTalk packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.NPC_TALK.getValue());
      mplew.write(4); // ?
      mplew.writeInt(packet.npcId());
      mplew.write(packet.messageType());
      mplew.write(packet.speaker());
      mplew.writeMapleAsciiString(packet.talk());
      mplew.write(HexTool.getByteArrayFromHexString(packet.endBytes()));
      return mplew.getPacket();
   }

   protected byte[] getDimensionalMirror(GetDimensionalMirror packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.NPC_TALK.getValue());
      mplew.write(4); // ?
      mplew.writeInt(9010022);
      mplew.write(0x0E);
      mplew.write(0);
      mplew.writeInt(0);
      mplew.writeMapleAsciiString(packet.talk());
      return mplew.getPacket();
   }

   protected byte[] getNPCTalkStyle(GetNPCTalkStyle packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.NPC_TALK.getValue());
      mplew.write(4); // ?
      mplew.writeInt(packet.npcId());
      mplew.write(7);
      mplew.write(0); //speaker
      mplew.writeMapleAsciiString(packet.talk());
      mplew.write(packet.styles().length);
      for (int style : packet.styles()) {
         mplew.writeInt(style);
      }
      return mplew.getPacket();
   }

   protected byte[] getNPCTalkNum(GetNPCTalkNum packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.NPC_TALK.getValue());
      mplew.write(4); // ?
      mplew.writeInt(packet.npcId());
      mplew.write(3);
      mplew.write(0); //speaker
      mplew.writeMapleAsciiString(packet.talk());
      mplew.writeInt(packet.theDef());
      mplew.writeInt(packet.min());
      mplew.writeInt(packet.max());
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   protected byte[] getNPCTalkText(GetNPCTalkText packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.NPC_TALK.getValue());
      mplew.write(4); // Doesn't matter
      mplew.writeInt(packet.npcId());
      mplew.write(2);
      mplew.write(0); //speaker
      mplew.writeMapleAsciiString(packet.talk());
      mplew.writeMapleAsciiString(packet.theDef());//:D
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   // thanks NPC Quiz packets thanks to Eric
   protected byte[] onAskQuiz(AskQuiz packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.NPC_TALK.getValue());
      mplew.write(packet.speakerTypeId());
      mplew.writeInt(packet.speakerTemplateId());
      mplew.write(0x6);
      mplew.write(0);
      mplew.write(packet.resCode());
      if (packet.resCode() == 0x0) {//fail has no bytes <3
         mplew.writeMapleAsciiString(packet.title());
         mplew.writeMapleAsciiString(packet.problemText());
         mplew.writeMapleAsciiString(packet.hintText());
         mplew.writeShort(packet.minInput());
         mplew.writeShort(packet.maxInput());
         mplew.writeInt(packet.remainInitialQuiz());
      }
      return mplew.getPacket();
   }

   protected byte[] onAskSpeedQuiz(AskSpeedQuiz packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.NPC_TALK.getValue());
      mplew.write(packet.speakerTypeId());
      mplew.writeInt(packet.speakerTemplateId());
      mplew.write(0x7);
      mplew.write(0);
      mplew.write(packet.resCode());
      if (packet.resCode() == 0x0) {//fail has no bytes <3
         mplew.writeInt(packet.theType());
         mplew.writeInt(packet.answer());
         mplew.writeInt(packet.correct());
         mplew.writeInt(packet.remain());
         mplew.writeInt(packet.remainInitialQuiz());
      }
      return mplew.getPacket();
   }
}