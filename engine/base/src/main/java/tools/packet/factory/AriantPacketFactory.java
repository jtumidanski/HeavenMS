package tools.packet.factory;

import java.util.Collections;

import client.MapleCharacter;
import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
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
      registry.setHandler(ShowAriantScoreboard.class, packet -> create(SendOpcode.ARIANT_ARENA_SHOW_RESULT, this::showScoreBoard, packet));
      registry.setHandler(UpdateAriantRanking.class, packet -> create(SendOpcode.ARIANT_ARENA_USER_SCORE, this::updateRanking, packet));
   }

   protected void showScoreBoard(MaplePacketLittleEndianWriter writer, ShowAriantScoreboard packet) {   // thanks lrenex for pointing match's end scoreboard packet
   }

   protected void updateIndividualRanking(MaplePacketLittleEndianWriter writer, final MapleCharacter chr, final int score) {
      updateRanking(writer, new UpdateAriantRanking(Collections.singletonList(new AriantScore(chr.getName(), score))));
   }

   protected void updateRanking(MaplePacketLittleEndianWriter writer, UpdateAriantRanking packet) {
      writer.write(packet.scores().size());
      for (AriantScore e : packet.scores()) {
         writer.writeMapleAsciiString(e.characterName());
         writer.writeInt(e.score());
      }
   }
}