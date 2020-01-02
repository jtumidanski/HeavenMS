package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.login.packet.CreateCharacterPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class CreateCharacterReader implements PacketReader<CreateCharacterPacket> {
   @Override
   public CreateCharacterPacket read(SeekableLittleEndianAccessor accessor) {
      String name = accessor.readMapleAsciiString();
      int job = accessor.readInt();
      int face = accessor.readInt();
      int hair = accessor.readInt();
      int hairColor = accessor.readInt();
      int skinColor = accessor.readInt();
      int top = accessor.readInt();
      int bottom = accessor.readInt();
      int shoes = accessor.readInt();
      int weapon = accessor.readInt();
      int gender = accessor.readByte();

      return new CreateCharacterPacket(name, job, face, hair, hairColor, skinColor, top, bottom, shoes, weapon, gender);
   }
}
