package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.SummonDamagePacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class SummonDamageReader implements PacketReader<SummonDamagePacket> {
   @Override
   public SummonDamagePacket read(SeekableLittleEndianAccessor accessor) {
      int objectId = accessor.readInt();
      accessor.skip(4);
      byte direction = accessor.readByte();
      int numAttacked = accessor.readByte();
      accessor.skip(8);

      int[] monsterObjectIds = new int[numAttacked];
      int[] damage = new int[numAttacked];

      for (int x = 0; x < numAttacked; x++) {
         monsterObjectIds[x] = accessor.readInt(); // attacked oid
         damage[x] = accessor.readInt();
      }

      return new SummonDamagePacket(objectId, direction, numAttacked, monsterObjectIds, damage);
   }
}
