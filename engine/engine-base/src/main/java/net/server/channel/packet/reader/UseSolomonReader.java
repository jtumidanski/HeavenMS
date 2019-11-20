package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.UseSolomonPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class UseSolomonReader implements PacketReader<UseSolomonPacket> {
   @Override
   public UseSolomonPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.readInt();
      short slot = accessor.readShort();
      int itemId = accessor.readInt();
      return new UseSolomonPacket(slot, itemId);
   }
}
