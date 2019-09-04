package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.login.packet.SetGenderPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class SetGenderReader implements PacketReader<SetGenderPacket> {
   @Override
   public SetGenderPacket read(SeekableLittleEndianAccessor accessor) {
      byte confirmed = accessor.readByte();
      byte gender = 0;
      if (confirmed == 0x01) {
         gender = accessor.readByte();
      }

      return new SetGenderPacket(confirmed, gender);
   }
}
