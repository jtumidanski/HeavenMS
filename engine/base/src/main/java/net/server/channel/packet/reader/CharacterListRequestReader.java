package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.login.packet.CharacterListRequestPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class CharacterListRequestReader implements PacketReader<CharacterListRequestPacket> {
   @Override
   public CharacterListRequestPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.readByte();
      return new CharacterListRequestPacket(accessor.readByte(), accessor.readByte() + 1);
   }
}
