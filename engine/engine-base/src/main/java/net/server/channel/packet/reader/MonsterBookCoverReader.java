package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.MonsterBookCoverPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class MonsterBookCoverReader implements PacketReader<MonsterBookCoverPacket> {
   @Override
   public MonsterBookCoverPacket read(SeekableLittleEndianAccessor accessor) {
      int id = accessor.readInt();
      return new MonsterBookCoverPacket(id);
   }
}
