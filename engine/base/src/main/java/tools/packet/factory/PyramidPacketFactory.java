package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.factory.AbstractPacketFactory;
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
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof PyramidScore) {
         return create(this::pyramidScore, packetInput);
      } else if (packetInput instanceof PyramidGuage) {
         return create(this::pyramidGauge, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] pyramidScore(PyramidScore packet) {//Type cannot be higher than 4 (Rank D), otherwise you'll crash
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(7);
      mplew.writeShort(SendOpcode.PYRAMID_SCORE.getValue());
      mplew.write(packet.score());
      mplew.writeInt(packet.exp());
      return mplew.getPacket();
   }

   protected byte[] pyramidGauge(PyramidGuage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(6);
      mplew.writeShort(SendOpcode.PYRAMID_GAUGE.getValue());
      mplew.writeInt(packet.guage());
      return mplew.getPacket();
   }

   protected byte[] MassacreResult(byte nRank, int nIncExp) {
      //CField_MassacreResult__OnMassacreResult @ 0x005617C5
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PYRAMID_SCORE.getValue()); //MASSACRERESULT | 0x009E
      mplew.write(nRank); //(0 - S) (1 - A) (2 - B) (3 - C) (4 - D) ( Else - Crash )
      mplew.writeInt(nIncExp);
      return mplew.getPacket();
   }

   protected byte[] GuildBoss_HealerMove(short nY) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_BOSS_HEALER_MOVE.getValue());
      mplew.writeShort(nY); //New Y Position
      return mplew.getPacket();
   }


   protected byte[] GuildBoss_PulleyStateChange(byte nState) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.GUILD_BOSS_PULLEY_STATE_CHANGE.getValue());
      mplew.write(nState);
      return mplew.getPacket();
   }

   protected byte[] Tournament__Tournament(byte nState, byte nSubState) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.TOURNAMENT.getValue());
      mplew.write(nState);
      mplew.write(nSubState);
      return mplew.getPacket();
   }

   protected byte[] Tournament__MatchTable(byte nState, byte nSubState) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.TOURNAMENT_MATCH_TABLE.getValue()); //Prompts CMatchTableDlg Modal
      return mplew.getPacket();
   }

   protected byte[] Tournament__SetPrize(byte bSetPrize, byte bHasPrize, int nItemID1, int nItemID2) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.TOURNAMENT_SET_PRIZE.getValue());

      //0 = "You have failed the set the prize. Please check the item number again."
      //1 = "You have successfully set the prize."
      mplew.write(bSetPrize);

      mplew.write(bHasPrize);

      if (bHasPrize != 0) {
         mplew.writeInt(nItemID1);
         mplew.writeInt(nItemID2);
      }

      return mplew.getPacket();
   }

   protected byte[] Tournament__UEW(byte nState) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.TOURNAMENT_UEW.getValue());

      //Is this a bitflag o.o ?
      //2 = "You have reached the finals by default."
      //4 = "You have reached the semifinals by default."
      //8 or 16 = "You have reached the round of %n by default." | Encodes nState as %n ?!
      mplew.write(nState);

      return mplew.getPacket();
   }
}