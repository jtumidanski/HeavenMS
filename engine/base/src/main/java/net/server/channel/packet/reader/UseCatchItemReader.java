package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.UseCatchItemPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class UseCatchItemReader implements PacketReader<UseCatchItemPacket> {
   @Override
   public UseCatchItemPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.readInt();
      accessor.readShort();
      int itemId = accessor.readInt();
      int monsterId = accessor.readInt();
      return new UseCatchItemPacket(itemId, monsterId);
   }
}
