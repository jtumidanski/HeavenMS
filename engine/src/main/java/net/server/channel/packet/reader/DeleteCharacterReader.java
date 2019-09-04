package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.login.packet.DeleteCharacterPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class DeleteCharacterReader implements PacketReader<DeleteCharacterPacket> {
   @Override
   public DeleteCharacterPacket read(SeekableLittleEndianAccessor accessor) {
      return new DeleteCharacterPacket(accessor.readMapleAsciiString(), accessor.readInt());
   }
}
