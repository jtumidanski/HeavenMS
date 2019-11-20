package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.GiveFamePacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class GiveFameReader implements PacketReader<GiveFamePacket> {
   @Override
   public GiveFamePacket read(SeekableLittleEndianAccessor accessor) {
      int characterId = accessor.readInt();
      int mode = accessor.readByte();
      return new GiveFamePacket(characterId, mode);
   }
}
