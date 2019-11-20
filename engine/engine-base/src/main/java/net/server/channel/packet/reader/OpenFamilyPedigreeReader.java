package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.family.OpenFamilyPedigreePacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class OpenFamilyPedigreeReader implements PacketReader<OpenFamilyPedigreePacket> {
   @Override
   public OpenFamilyPedigreePacket read(SeekableLittleEndianAccessor accessor) {
      String characterName = accessor.readMapleAsciiString();
      return new OpenFamilyPedigreePacket(characterName);
   }
}
