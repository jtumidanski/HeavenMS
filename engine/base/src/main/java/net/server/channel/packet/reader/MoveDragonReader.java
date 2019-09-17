package net.server.channel.packet.reader;

import java.awt.Point;

import net.server.channel.packet.movement.MoveDragonPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class MoveDragonReader extends AbstractMovementReader<MoveDragonPacket> {
   @Override
   public MoveDragonPacket read(SeekableLittleEndianAccessor accessor) {
      Point startPos = readStartingPosition(accessor);
      return producePacket(accessor, 0, (hasMovement, movementDataList, movementList) -> new MoveDragonPacket(startPos, hasMovement, movementDataList, movementList));
   }
}
