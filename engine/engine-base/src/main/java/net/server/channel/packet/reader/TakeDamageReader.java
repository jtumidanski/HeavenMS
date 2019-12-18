package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.TakeDamagePacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class TakeDamageReader implements PacketReader<TakeDamagePacket> {
   @Override
   public TakeDamagePacket read(SeekableLittleEndianAccessor accessor) {
      accessor.readInt();
      byte damageFrom = accessor.readByte();
      byte element = accessor.readByte(); //Element
      int damage = accessor.readInt();
      int oid = 0;
      int monsterIdFrom = 0;
      if (damageFrom != -3 && damageFrom != -4) {
         monsterIdFrom = accessor.readInt();
         oid = accessor.readInt();
      }

      byte direction = accessor.readByte();
      return new TakeDamagePacket(damageFrom, element, damage, monsterIdFrom, oid, direction);
   }
}
