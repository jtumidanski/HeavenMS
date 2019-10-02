package tools.packet.factory;

import java.awt.Point;
import java.util.List;

import net.opcodes.SendOpcode;
import server.maps.MapleDragon;
import server.movement.LifeMovementFragment;
import tools.FilePrinter;
import tools.data.output.LittleEndianWriter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.factory.AbstractPacketFactory;
import tools.packet.movement.MoveDragon;
import tools.packet.movement.MoveMonster;
import tools.packet.movement.MoveMonsterResponse;
import tools.packet.movement.MovePet;
import tools.packet.movement.MovePlayer;
import tools.packet.movement.MoveSummon;

public class MovementPacketFactory extends AbstractPacketFactory {
   private static MovementPacketFactory instance;

   public static MovementPacketFactory getInstance() {
      if (instance == null) {
         instance = new MovementPacketFactory();
      }
      return instance;
   }

   private MovementPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof MoveMonsterResponse) {
         return create(this::moveMonsterResponse, packetInput);
      } else if (packetInput instanceof MovePlayer) {
         return create(this::movePlayer, packetInput);
      } else if (packetInput instanceof MoveSummon) {
         return create(this::moveSummon, packetInput);
      } else if (packetInput instanceof MoveMonster) {
         return create(this::moveMonster, packetInput);
      } else if (packetInput instanceof MovePet) {
         return create(this::movePet, packetInput);
      } else if (packetInput instanceof MoveDragon) {
         return create(this::moveDragon, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   /**
    * Gets a response to a move monster packet.
    *
    * @return The move response packet.
    */
   protected byte[] moveMonsterResponse(MoveMonsterResponse packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(13);
      mplew.writeShort(SendOpcode.MOVE_MONSTER_RESPONSE.getValue());
      mplew.writeInt(packet.objectId());
      mplew.writeShort(packet.moveId());
      mplew.writeBool(packet.useSkills());
      mplew.writeShort(packet.currentMp());
      mplew.write(packet.skillId());
      mplew.write(packet.skillLevel());
      return mplew.getPacket();
   }

   protected void rebroadcastMovementList(LittleEndianWriter lew, List<Byte> movementDataList) {
      //movement command length is sent by client, probably not a big issue? (could be calculated on server)
      //if multiple write/reads are slow, could use a (cached?) byte[] buffer
      for (int i = 0; i < movementDataList.size(); i++) {
         lew.write(movementDataList.get(i));
      }
   }

   protected byte[] movePlayer(MovePlayer packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MOVE_PLAYER.getValue());
      mplew.writeInt(packet.characterId());
      mplew.writeInt(0);
      rebroadcastMovementList(mplew, packet.movementList());
      return mplew.getPacket();
   }

   protected byte[] moveSummon(MoveSummon packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MOVE_SUMMON.getValue());
      mplew.writeInt(packet.characterId());
      mplew.writeInt(packet.objectId());
      mplew.writePos(packet.startPosition());
      rebroadcastMovementList(mplew, packet.movementList());
      return mplew.getPacket();
   }

   protected byte[] moveMonster(MoveMonster packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MOVE_MONSTER.getValue());
      mplew.writeInt(packet.objectId());
      mplew.write(0);
      mplew.writeBool(packet.skillPossible());
      mplew.write(packet.skill());
      mplew.write(packet.skillId());
      mplew.write(packet.skillLevel());
      mplew.writeShort(packet.option());
      mplew.writePos(packet.startPosition());
      rebroadcastMovementList(mplew, packet.movementList());
      return mplew.getPacket();
   }

   protected byte[] movePet(MovePet packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MOVE_PET.getValue());
      mplew.writeInt(packet.characterId());
      mplew.write(packet.slot());
      mplew.writeInt(packet.petId());
      serializeMovementList(mplew, packet.movementList());
      return mplew.getPacket();
   }

   protected void serializeMovementList(LittleEndianWriter lew, List<LifeMovementFragment> moves) {
      lew.write(moves.size());
      for (LifeMovementFragment move : moves) {
         move.serialize(lew);
      }
   }

   protected byte[] moveDragon(MoveDragon packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MOVE_DRAGON.getValue());
      mplew.writeInt(packet.ownerId());
      mplew.writePos(packet.startPosition());
      rebroadcastMovementList(mplew, packet.movementList());
      return mplew.getPacket();
   }
}