package net.server.channel.packet.reader;

import net.server.channel.packet.movement.MovePlayerPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class MovePlayerReader extends AbstractMovementReader<MovePlayerPacket> {
   @Override
   public MovePlayerPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.skip(9);
      return producePacket(accessor, 0, MovePlayerPacket::new);
   }
}
