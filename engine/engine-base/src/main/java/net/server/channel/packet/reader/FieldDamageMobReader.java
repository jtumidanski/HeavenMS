package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.FieldDamageMobPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class FieldDamageMobReader implements PacketReader<FieldDamageMobPacket> {
   @Override
   public FieldDamageMobPacket read(SeekableLittleEndianAccessor accessor) {
      int mobOid = accessor.readInt();    // packet structure found thanks to Darter (Rajan)
      int dmg = accessor.readInt();
      return new FieldDamageMobPacket(mobOid, dmg);
   }
}
