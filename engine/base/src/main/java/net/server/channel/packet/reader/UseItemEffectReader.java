package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.UseItemEffectPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class UseItemEffectReader implements PacketReader<UseItemEffectPacket> {
   @Override
   public UseItemEffectPacket read(SeekableLittleEndianAccessor accessor) {
      int itemId = accessor.readInt();
      return new UseItemEffectPacket(itemId);
   }
}
