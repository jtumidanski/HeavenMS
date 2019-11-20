package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.MobBanishPlayerPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class MobBanishPlayerReader implements PacketReader<MobBanishPlayerPacket> {
   @Override
   public MobBanishPlayerPacket read(SeekableLittleEndianAccessor accessor) {
      int mobid = accessor.readInt();     // mob banish handling detected thanks to MedicOP
      return new MobBanishPlayerPacket(mobid);
   }
}
