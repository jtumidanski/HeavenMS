package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.TransferNameResultPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class TransferNameResultReader implements PacketReader<TransferNameResultPacket> {
   @Override
   public TransferNameResultPacket read(SeekableLittleEndianAccessor accessor) {
      return new TransferNameResultPacket(accessor.readMapleAsciiString());
   }
}
