package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.pyramid.PyramidGuage;
import tools.packet.pyramid.PyramidScore;

public class PyramidPacketFactory extends AbstractPacketFactory {
   private static PyramidPacketFactory instance;

   public static PyramidPacketFactory getInstance() {
      if (instance == null) {
         instance = new PyramidPacketFactory();
      }
      return instance;
   }

   private PyramidPacketFactory() {
      registry.setHandler(PyramidScore.class, packet -> create(SendOpcode.PYRAMID_SCORE, this::pyramidScore, packet, 7));
      registry.setHandler(PyramidGuage.class, packet -> create(SendOpcode.PYRAMID_GAUGE, this::pyramidGauge, packet, 6));
   }

   protected void pyramidScore(MaplePacketLittleEndianWriter writer, PyramidScore packet) {//Type cannot be higher than 4 (MaplePacketLittleEndianWriter writer, Rank D), otherwise you'll crash
      writer.write(packet.score());
      writer.writeInt(packet.exp());
   }

   protected void pyramidGauge(MaplePacketLittleEndianWriter writer, PyramidGuage packet) {
      writer.writeInt(packet.guage());
   }

   protected void MassacreResult(MaplePacketLittleEndianWriter writer, byte nRank, int nIncExp) {
//      //CField_MassacreResult__OnMassacreResult @ 0x005617C5
//      final MaplePacketLittleEndianWriter writer = new MaplePacketLittleEndianWriter();
//      writer.writeShort(SendOpcode.PYRAMID_SCORE.getValue()); //MASSACRERESULT | 0x009E
//      writer.write(nRank); //(0 - S) (1 - A) (2 - B) (3 - C) (4 - D) ( Else - Crash )
//      writer.writeInt(nIncExp);
//      return writer.getPacket();
   }

   protected void GuildBoss_HealerMove(MaplePacketLittleEndianWriter writer, short nY) {
//      final MaplePacketLittleEndianWriter writer = new MaplePacketLittleEndianWriter();
//      writer.writeShort(SendOpcode.GUILD_BOSS_HEALER_MOVE.getValue());
//      writer.writeShort(nY); //New Y Position
//      return writer.getPacket();
   }


   protected void GuildBoss_PulleyStateChange(MaplePacketLittleEndianWriter writer, byte nState) {
//      final MaplePacketLittleEndianWriter writer = new MaplePacketLittleEndianWriter();
//      writer.writeShort(SendOpcode.GUILD_BOSS_PULLEY_STATE_CHANGE.getValue());
//      writer.write(nState);
//      return writer.getPacket();
   }

   protected void Tournament__Tournament(MaplePacketLittleEndianWriter writer, byte nState, byte nSubState) {
//      final MaplePacketLittleEndianWriter writer = new MaplePacketLittleEndianWriter();
//      writer.writeShort(SendOpcode.TOURNAMENT.getValue());
//      writer.write(nState);
//      writer.write(nSubState);
//      return writer.getPacket();
   }

   protected void Tournament__MatchTable(MaplePacketLittleEndianWriter writer, byte nState, byte nSubState) {
//      final MaplePacketLittleEndianWriter writer = new MaplePacketLittleEndianWriter();
//      writer.writeShort(SendOpcode.TOURNAMENT_MATCH_TABLE.getValue()); //Prompts CMatchTableDlg Modal
//      return writer.getPacket();
   }

   protected void Tournament__SetPrize(MaplePacketLittleEndianWriter writer, byte bSetPrize, byte bHasPrize, int nItemID1, int nItemID2) {
//      final MaplePacketLittleEndianWriter writer = new MaplePacketLittleEndianWriter();
//      writer.writeShort(SendOpcode.TOURNAMENT_SET_PRIZE.getValue());
//
//      //0 = "You have failed the set the prize. Please check the item number again."
//      //1 = "You have successfully set the prize."
//      writer.write(bSetPrize);
//
//      writer.write(bHasPrize);
//
//      if (bHasPrize != 0) {
//         writer.writeInt(nItemID1);
//         writer.writeInt(nItemID2);
//      }
//
//      return writer.getPacket();
   }

   protected void Tournament__UEW(MaplePacketLittleEndianWriter writer, byte nState) {
//      final MaplePacketLittleEndianWriter writer = new MaplePacketLittleEndianWriter();
//      writer.writeShort(SendOpcode.TOURNAMENT_UEW.getValue());
//
//      //Is this a bitflag o.o ?
//      //2 = "You have reached the finals by default."
//      //4 = "You have reached the semifinals by default."
//      //8 or 16 = "You have reached the round of %n by default." | Encodes nState as %n ?!
//      writer.write(nState);
//
//      return writer.getPacket();
   }
}