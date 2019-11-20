package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.MobDamageMobPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class MobDamageMobReader implements PacketReader<MobDamageMobPacket> {
   @Override
   public MobDamageMobPacket read(SeekableLittleEndianAccessor accessor) {
      int from = accessor.readInt();
      accessor.readInt();
      int to = accessor.readInt();
      boolean magic = accessor.readByte() == 0;
      int dmg = accessor.readInt();
      return new MobDamageMobPacket(from, to, magic, dmg);
   }
}
