package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.GrenadeEffectPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class GrenadeEffectReader implements PacketReader<GrenadeEffectPacket> {
   @Override
   public GrenadeEffectPacket read(SeekableLittleEndianAccessor accessor) {
      int x = accessor.readInt();
      int y = accessor.readInt();
      int keyDown = accessor.readInt();
      int skillId = accessor.readInt();
      return new GrenadeEffectPacket(x, y, keyDown, skillId);
   }
}
