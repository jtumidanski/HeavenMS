package net.server.channel.packet.reader;

import java.awt.Point;

import net.server.channel.packet.movement.MoveSummonPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class MoveSummonReader extends AbstractMovementReader<MoveSummonPacket> {
   @Override
   public MoveSummonPacket read(SeekableLittleEndianAccessor accessor) {
      int oid = accessor.readInt();
      Point startPos = readStartingPosition(accessor);
      return producePacket(accessor, 0, (hasMovement, movementDataList, movementList) -> new MoveSummonPacket(oid, startPos, hasMovement, movementDataList, movementList));
   }
}
