package net.server.channel.packet.reader;

import constants.skills.DarkKnight;
import net.server.PacketReader;
import net.server.channel.packet.BeholderPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class BeholderReader implements PacketReader<BeholderPacket> {
   @Override
   public BeholderPacket read(SeekableLittleEndianAccessor accessor) {
      int oid = accessor.readInt();
      int skillId = accessor.readInt();
      if (skillId == DarkKnight.AURA_OF_BEHOLDER) {
         accessor.readShort();
      } else if (skillId == DarkKnight.HEX_OF_BEHOLDER) {
         accessor.readByte();
      }
      return new BeholderPacket(oid, skillId);
   }
}
