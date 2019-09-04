package net.server;

import tools.data.input.SeekableLittleEndianAccessor;

public interface PacketReader<T extends MaplePacket> {
   T read(SeekableLittleEndianAccessor slea);
}
