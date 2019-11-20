package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.ReactorHitPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class ReactorHitReader implements PacketReader<ReactorHitPacket> {
   @Override
   public ReactorHitPacket read(SeekableLittleEndianAccessor accessor) {
      int oid = accessor.readInt();
      int charPos = accessor.readInt();
      short stance = accessor.readShort();
      accessor.skip(4);
      int skillId = accessor.readInt();
      return new ReactorHitPacket(oid, charPos, stance, skillId);
   }
}
