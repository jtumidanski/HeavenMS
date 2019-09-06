package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.TransferNamePacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class TransferNameReader implements PacketReader<TransferNamePacket> {
   @Override
   public TransferNamePacket read(SeekableLittleEndianAccessor accessor) {
      return new TransferNamePacket(accessor.readInt(), accessor.readInt());
   }
}
