package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.MonsterBombPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class MonsterBombReader implements PacketReader<MonsterBombPacket> {
   @Override
   public MonsterBombPacket read(SeekableLittleEndianAccessor accessor) {
      int oid = accessor.readInt();
      return new MonsterBombPacket(oid);
   }
}
