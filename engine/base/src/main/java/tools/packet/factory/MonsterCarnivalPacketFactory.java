package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;
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
      registry.setHandler(MonsterCarnivalStart.class, packet -> create(SendOpcode.MONSTER_CARNIVAL_OBTAINED_CP, this::startMonsterCarnival, packet));
      registry.setHandler(MonsterCarnivalPlayerDied.class, packet -> create(SendOpcode.MONSTER_CARNIVAL_PARTY_CP, this::playerDiedMessage, packet));
      registry.setHandler(MonsterCarnivalMessage.class, packet -> create(SendOpcode.MONSTER_CARNIVAL_MESSAGE, this::message, packet, 3));
      registry.setHandler(MonsterCarnivalPlayerSummoned.class, packet -> create(SendOpcode.MONSTER_CARNIVAL_SUMMON, this::playerSummoned, packet));
      registry.setHandler(MonsterCarnivalPointObtained.class, packet -> create(SendOpcode.MONSTER_CARNIVAL_DIED, this::obtainedPoints, packet));
      registry.setHandler(MonsterCarnivalPartyPoints.class, packet -> create(SendOpcode.MONSTER_CARNIVAL_START, this::partyPoints, packet, 25));
   }

   protected void obtainedPoints(MaplePacketLittleEndianWriter writer, MonsterCarnivalPointObtained packet) { // CPQ
      writer.writeShort(packet.currentPoints());
      writer.writeShort(packet.totalPoints());
   }

   protected void partyPoints(MaplePacketLittleEndianWriter writer, MonsterCarnivalPartyPoints packet) { // CPQ
      writer.write(packet.team()); // team?
      writer.writeShort(packet.currentPoints());
      writer.writeShort(packet.totalPoints());
   }

   protected void message(MaplePacketLittleEndianWriter writer, MonsterCarnivalMessage packet) {
      writer.write(packet.message()); // Message
   }

   protected void playerSummoned(MaplePacketLittleEndianWriter writer, MonsterCarnivalPlayerSummoned packet) {
      writer.write(packet.tab());
      writer.write(packet.number());
      writer.writeMapleAsciiString(packet.name());
   }

   protected void playerDiedMessage(MaplePacketLittleEndianWriter writer, MonsterCarnivalPlayerDied packet) { // CPQ
      writer.write(packet.team()); // team
      writer.writeMapleAsciiString(packet.name());
      writer.write(packet.lostPoints());
   }

   protected void startMonsterCarnival(MaplePacketLittleEndianWriter writer, MonsterCarnivalStart packet) {
      writer.write(packet.team()); // team
      writer.writeShort(packet.freePoints()); // Obtained CP - Used CP
      writer.writeShort(packet.totalPoints()); // Total Obtained CP
      writer.writeShort(packet.teamFreePoints()); // Obtained CP - Used CP of the team
      writer.writeShort(packet.teamTotalPoints()); // Total Obtained CP of the team
      writer.writeShort(packet.oppositionFreePoints()); // Obtained CP - Used CP of the team
      writer.writeShort(packet.oppositionTotalPoints()); // Total Obtained CP of the team
      writer.writeShort(0); // Probably useless nexon shit
      writer.writeLong(0); // Probably useless nexon shit
   }
}