package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.CoconutPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class CoconutReader implements PacketReader<CoconutPacket> {
   @Override
   public CoconutPacket read(SeekableLittleEndianAccessor accessor) {
      /*CB 00 A6 00 06 01
       * A6 00 = coconut id
       * 06 01 = ?
       */
      int id = accessor.readShort();
      return new CoconutPacket(id);
   }
}
