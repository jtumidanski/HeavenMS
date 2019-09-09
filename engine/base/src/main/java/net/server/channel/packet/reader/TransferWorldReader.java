package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.TransferWorldPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class TransferWorldReader implements PacketReader<TransferWorldPacket> {
   @Override
   public TransferWorldPacket read(SeekableLittleEndianAccessor accessor) {
      return new TransferWorldPacket(accessor.readInt(), accessor.readInt());
   }
}
