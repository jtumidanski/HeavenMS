package tools.packet.factory;

import java.util.Collections;

import client.MapleCharacter;
import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.pq.ariant.AriantScore;
import tools.packet.pq.ariant.ShowAriantScoreboard;
import tools.packet.pq.ariant.UpdateAriantRanking;

public class AriantPacketFactory extends AbstractPacketFactory {
   private static AriantPacketFactory instance;

   public static AriantPacketFactory getInstance() {
      if (instance == null) {
         instance = new AriantPacketFactory();
      }
      return instance;
   }

   private AriantPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof ShowAriantScoreboard) {
         return create(this::showScoreBoard, packetInput);
      } else if (packetInput instanceof UpdateAriantRanking) {
         return create(this::updateRanking, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] showScoreBoard(ShowAriantScoreboard packet) {   // thanks lrenex for pointing match's end scoreboard packet
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ARIANT_ARENA_SHOW_RESULT.getValue());
      return mplew.getPacket();
   }

   protected byte[] updateIndividualRanking(final MapleCharacter chr, final int score) {
      return updateRanking(new UpdateAriantRanking(Collections.singletonList(new AriantScore(chr.getName(), score))));
   }

   protected byte[] updateRanking(UpdateAriantRanking packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ARIANT_ARENA_USER_SCORE.getValue());
      mplew.write(packet.scores().size());
      for (AriantScore e : packet.scores()) {
         mplew.writeMapleAsciiString(e.characterName());
         mplew.writeInt(e.score());
      }
      return mplew.getPacket();
   }
}