package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.wedding.BaseWeddingTalkPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class WeddingTalkReader implements PacketReader<BaseWeddingTalkPacket> {
   @Override
   public BaseWeddingTalkPacket read(SeekableLittleEndianAccessor accessor) {
      byte action = accessor.readByte();
      return new BaseWeddingTalkPacket(action);
   }
}
