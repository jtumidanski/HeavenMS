package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.CharacterInfoRequestPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class CharacterInfoRequestReader implements PacketReader<CharacterInfoRequestPacket> {
   @Override
   public CharacterInfoRequestPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.skip(4);
      int cid = accessor.readInt();
      return new CharacterInfoRequestPacket(cid);
   }
}
