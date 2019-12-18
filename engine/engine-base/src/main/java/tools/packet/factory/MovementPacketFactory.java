package tools.packet.factory;

import java.util.List;

import server.movement.LifeMovementFragment;
import tools.data.output.LittleEndianWriter;
import tools.data.output.MaplePacketLittleEndianWriter;
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
      Handler.handle(MoveMonsterResponse.class).decorate(this::moveMonsterResponse).size(13).register(registry);
      Handler.handle(MovePlayer.class).decorate(this::movePlayer).register(registry);
      Handler.handle(MoveSummon.class).decorate(this::moveSummon).register(registry);
      Handler.handle(MoveMonster.class).decorate(this::moveMonster).register(registry);
      Handler.handle(MovePet.class).decorate(this::movePet).register(registry);
      Handler.handle(MoveDragon.class).decorate(this::moveDragon).register(registry);
   }

   /**
    * Gets a response to a move monster packet.
    *
    * @return The move response packet.
    */
   protected void moveMonsterResponse(MaplePacketLittleEndianWriter writer, MoveMonsterResponse packet) {
      writer.writeInt(packet.objectId());
      writer.writeShort(packet.moveId());
      writer.writeBool(packet.useSkills());
      writer.writeShort(packet.currentMp());
      writer.write(packet.skillId());
      writer.write(packet.skillLevel());
   }

   protected void rebroadcastMovementList(LittleEndianWriter lew, List<Byte> movementDataList) {
      //movement command length is sent by client, probably not a big issue? (could be calculated on server)
      //if multiple write/reads are slow, could use a (cached?) byte[] buffer
      for (Byte aByte : movementDataList) {
         lew.write(aByte);
      }
   }

   protected void movePlayer(MaplePacketLittleEndianWriter writer, MovePlayer packet) {
      writer.writeInt(packet.characterId());
      writer.writeInt(0);
      rebroadcastMovementList(writer, packet.movementList());
   }

   protected void moveSummon(MaplePacketLittleEndianWriter writer, MoveSummon packet) {
      writer.writeInt(packet.characterId());
      writer.writeInt(packet.objectId());
      writer.writePos(packet.startPosition());
      rebroadcastMovementList(writer, packet.movementList());
   }

   protected void moveMonster(MaplePacketLittleEndianWriter writer, MoveMonster packet) {
      writer.writeInt(packet.objectId());
      writer.write(0);
      writer.writeBool(packet.skillPossible());
      writer.write(packet.skill());
      writer.write(packet.skillId());
      writer.write(packet.skillLevel());
      writer.writeShort(packet.option());
      writer.writePos(packet.startPosition());
      rebroadcastMovementList(writer, packet.movementList());
   }

   protected void movePet(MaplePacketLittleEndianWriter writer, MovePet packet) {
      writer.writeInt(packet.characterId());
      writer.write(packet.slot());
      writer.writeInt(packet.petId());
      serializeMovementList(writer, packet.movementList());
   }

   protected void serializeMovementList(LittleEndianWriter lew, List<LifeMovementFragment> moves) {
      lew.write(moves.size());
      for (LifeMovementFragment move : moves) {
         move.serialize(lew);
      }
   }

   protected void moveDragon(MaplePacketLittleEndianWriter writer, MoveDragon packet) {
      writer.writeInt(packet.ownerId());
      writer.writePos(packet.startPosition());
      rebroadcastMovementList(writer, packet.movementList());
   }
}