package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.CancelItemEffectPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class CancelItemEffectReader implements PacketReader<CancelItemEffectPacket> {
   @Override
   public CancelItemEffectPacket read(SeekableLittleEndianAccessor accessor) {
      int itemId = -accessor.readInt();
      return new CancelItemEffectPacket(itemId);
   }
}
