package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.MonsterCarnivalPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class MonsterCarnivalReader implements PacketReader<MonsterCarnivalPacket> {
   @Override
   public MonsterCarnivalPacket read(SeekableLittleEndianAccessor accessor) {
      int tab = accessor.readByte();
      int num = accessor.readByte();
      return new MonsterCarnivalPacket(tab, num);
   }
}
