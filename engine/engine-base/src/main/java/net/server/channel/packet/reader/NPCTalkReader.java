package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.NPCTalkPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class NPCTalkReader implements PacketReader<NPCTalkPacket> {
   @Override
   public NPCTalkPacket read(SeekableLittleEndianAccessor accessor) {
      return new NPCTalkPacket(accessor.readInt());
   }
}
