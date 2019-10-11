package tools.packet.factory;

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
      Handler.handle(GetNPCTalk.class).decorate(this::getNPCTalk).register(registry);
      Handler.handle(GetDimensionalMirror.class).decorate(this::getDimensionalMirror).register(registry);
      Handler.handle(GetNPCTalkStyle.class).decorate(this::getNPCTalkStyle).register(registry);
      Handler.handle(GetNPCTalkNum.class).decorate(this::getNPCTalkNum).register(registry);
      Handler.handle(GetNPCTalkText.class).decorate(this::getNPCTalkText).register(registry);
      Handler.handle(AskQuiz.class).decorate(this::onAskQuiz).register(registry);
      Handler.handle(AskSpeedQuiz.class).decorate(this::onAskSpeedQuiz).register(registry);
   }

   /**
    * Possible values for <code>speaker</code>:<br> 0: Npc talking (left)<br>
    * 1: Npc talking (right)<br> 2: Player talking (left)<br> 3: Player talking
    * (left)<br>
    */
   protected void getNPCTalk(MaplePacketLittleEndianWriter writer, GetNPCTalk packet) {
      writer.write(4); // ?
      writer.writeInt(packet.npcId());
      writer.write(packet.messageType());
      writer.write(packet.speaker());
      writer.writeMapleAsciiString(packet.talk());
      writer.write(HexTool.getByteArrayFromHexString(packet.endBytes()));
   }

   protected void getDimensionalMirror(MaplePacketLittleEndianWriter writer, GetDimensionalMirror packet) {
      writer.write(4); // ?
      writer.writeInt(9010022);
      writer.write(0x0E);
      writer.write(0);
      writer.writeInt(0);
      writer.writeMapleAsciiString(packet.talk());
   }

   protected void getNPCTalkStyle(MaplePacketLittleEndianWriter writer, GetNPCTalkStyle packet) {
      writer.write(4); // ?
      writer.writeInt(packet.npcId());
      writer.write(7);
      writer.write(0); //speaker
      writer.writeMapleAsciiString(packet.talk());
      writer.write(packet.styles().length);
      for (int style : packet.styles()) {
         writer.writeInt(style);
      }
   }

   protected void getNPCTalkNum(MaplePacketLittleEndianWriter writer, GetNPCTalkNum packet) {
      writer.write(4); // ?
      writer.writeInt(packet.npcId());
      writer.write(3);
      writer.write(0); //speaker
      writer.writeMapleAsciiString(packet.talk());
      writer.writeInt(packet.theDef());
      writer.writeInt(packet.min());
      writer.writeInt(packet.max());
      writer.writeInt(0);
   }

   protected void getNPCTalkText(MaplePacketLittleEndianWriter writer, GetNPCTalkText packet) {
      writer.write(4); // Doesn't matter
      writer.writeInt(packet.npcId());
      writer.write(2);
      writer.write(0); //speaker
      writer.writeMapleAsciiString(packet.talk());
      writer.writeMapleAsciiString(packet.theDef());//:D
      writer.writeInt(0);
   }

   // thanks NPC Quiz packets thanks to Eric
   protected void onAskQuiz(MaplePacketLittleEndianWriter writer, AskQuiz packet) {
      writer.write(packet.speakerTypeId());
      writer.writeInt(packet.speakerTemplateId());
      writer.write(0x6);
      writer.write(0);
      writer.write(packet.resCode());
      if (packet.resCode() == 0x0) {//fail has no bytes <3
         writer.writeMapleAsciiString(packet.title());
         writer.writeMapleAsciiString(packet.problemText());
         writer.writeMapleAsciiString(packet.hintText());
         writer.writeShort(packet.minInput());
         writer.writeShort(packet.maxInput());
         writer.writeInt(packet.remainInitialQuiz());
      }
   }

   protected void onAskSpeedQuiz(MaplePacketLittleEndianWriter writer, AskSpeedQuiz packet) {
      writer.write(packet.speakerTypeId());
      writer.writeInt(packet.speakerTemplateId());
      writer.write(0x7);
      writer.write(0);
      writer.write(packet.resCode());
      if (packet.resCode() == 0x0) {//fail has no bytes <3
         writer.writeInt(packet.theType());
         writer.writeInt(packet.answer());
         writer.writeInt(packet.correct());
         writer.writeInt(packet.remain());
         writer.writeInt(packet.remainInitialQuiz());
      }
   }
}