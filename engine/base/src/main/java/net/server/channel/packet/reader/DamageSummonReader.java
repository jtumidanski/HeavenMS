package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.DamageSummonPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class DamageSummonReader implements PacketReader<DamageSummonPacket> {
   @Override
   public DamageSummonPacket read(SeekableLittleEndianAccessor accessor) {
      int oid = accessor.readInt();
      accessor.skip(1);   // -1
      int damage = accessor.readInt();
      int monsterIdFrom = accessor.readInt();
      return new DamageSummonPacket(oid, damage, monsterIdFrom);
   }
}
