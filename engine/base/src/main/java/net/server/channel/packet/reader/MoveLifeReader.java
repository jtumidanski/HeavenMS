package net.server.channel.packet.reader;

import net.server.channel.packet.movement.MoveLifePacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class MoveLifeReader extends AbstractMovementReader<MoveLifePacket> {
   @Override
   public MoveLifePacket read(SeekableLittleEndianAccessor accessor) {
      int objectId = accessor.readInt();
      short moveId = accessor.readShort();

      byte pNibbles = accessor.readByte();
      byte rawActivity = accessor.readByte();
      int skillId = accessor.readByte() & 0xff;
      int skillLv = accessor.readByte() & 0xff;
      short pOption = accessor.readShort();
      accessor.skip(8);

      accessor.readByte();
      accessor.readInt(); // whatever
      short startX = accessor.readShort(); // hmm.. startpos?
      short startY = accessor.readShort(); // hmm...
      return producePacket(accessor, -2, (hasMovement, movementDataList, movementList) ->
            new MoveLifePacket(objectId, moveId, pNibbles, rawActivity, skillId, skillLv, pOption, startX, startY, hasMovement, movementDataList, movementList));
   }
}
