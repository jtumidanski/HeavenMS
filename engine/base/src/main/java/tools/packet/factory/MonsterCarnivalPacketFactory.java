package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.monster.carnival.MonsterCarnivalMessage;
import tools.packet.monster.carnival.MonsterCarnivalPartyPoints;
import tools.packet.monster.carnival.MonsterCarnivalPlayerDied;
import tools.packet.monster.carnival.MonsterCarnivalPlayerSummoned;
import tools.packet.monster.carnival.MonsterCarnivalPointObtained;
import tools.packet.monster.carnival.MonsterCarnivalStart;

public class MonsterCarnivalPacketFactory extends AbstractPacketFactory {
   private static MonsterCarnivalPacketFactory instance;

   public static MonsterCarnivalPacketFactory getInstance() {
      if (instance == null) {
         instance = new MonsterCarnivalPacketFactory();
      }
      return instance;
   }

   private MonsterCarnivalPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof MonsterCarnivalStart) {
         return create(this::startMonsterCarnival, packetInput);
      } else if (packetInput instanceof MonsterCarnivalPlayerDied) {
         return create(this::playerDiedMessage, packetInput);
      } else if (packetInput instanceof MonsterCarnivalPlayerSummoned) {
         return create(this::playerSummoned, packetInput);
      } else if (packetInput instanceof MonsterCarnivalMessage) {
         return create(this::message, packetInput);
      } else if (packetInput instanceof MonsterCarnivalPointObtained) {
         return create(this::obtainedPoints, packetInput);
      } else if (packetInput instanceof MonsterCarnivalPartyPoints) {
         return create(this::partyPoints, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] obtainedPoints(MonsterCarnivalPointObtained packet) { // CPQ
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MONSTER_CARNIVAL_OBTAINED_CP.getValue());
      mplew.writeShort(packet.currentPoints());
      mplew.writeShort(packet.totalPoints());
      return mplew.getPacket();
   }

   protected byte[] partyPoints(MonsterCarnivalPartyPoints packet) { // CPQ
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MONSTER_CARNIVAL_PARTY_CP.getValue());
      mplew.write(packet.team()); // team?
      mplew.writeShort(packet.currentPoints());
      mplew.writeShort(packet.totalPoints());
      return mplew.getPacket();
   }

   protected byte[] message(MonsterCarnivalMessage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.MONSTER_CARNIVAL_MESSAGE.getValue());
      mplew.write(packet.message()); // Message
      return mplew.getPacket();
   }

   protected byte[] playerSummoned(MonsterCarnivalPlayerSummoned packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MONSTER_CARNIVAL_SUMMON.getValue());
      mplew.write(packet.tab());
      mplew.write(packet.number());
      mplew.writeMapleAsciiString(packet.name());
      return mplew.getPacket();
   }

   protected byte[] playerDiedMessage(MonsterCarnivalPlayerDied packet) { // CPQ
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MONSTER_CARNIVAL_DIED.getValue());
      mplew.write(packet.team()); // team
      mplew.writeMapleAsciiString(packet.name());
      mplew.write(packet.lostPoints());
      return mplew.getPacket();
   }

   protected byte[] startMonsterCarnival(MonsterCarnivalStart packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(25);
      mplew.writeShort(SendOpcode.MONSTER_CARNIVAL_START.getValue());
      mplew.write(packet.team()); // team
      mplew.writeShort(packet.freePoints()); // Obtained CP - Used CP
      mplew.writeShort(packet.totalPoints()); // Total Obtained CP
      mplew.writeShort(packet.teamFreePoints()); // Obtained CP - Used CP of the team
      mplew.writeShort(packet.teamTotalPoints()); // Total Obtained CP of the team
      mplew.writeShort(packet.oppositionFreePoints()); // Obtained CP - Used CP of the team
      mplew.writeShort(packet.oppositionTotalPoints()); // Total Obtained CP of the team
      mplew.writeShort(0); // Probably useless nexon shit
      mplew.writeLong(0); // Probably useless nexon shit
      return mplew.getPacket();
   }
}