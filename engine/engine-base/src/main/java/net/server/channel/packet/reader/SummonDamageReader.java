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
      accessor.skip(8); //Thanks Gerald :D, I failed lol (mob x,y and summon x,y)

      int[] monsterOids = new int[numAttacked];
      int[] damage = new int[numAttacked];

      for (int x = 0; x < numAttacked; x++) {
         monsterOids[x] = accessor.readInt(); // attacked oid
         accessor.skip(18);
         damage[x] = accessor.readInt();
      }

      return new SummonDamagePacket(objectId, direction, numAttacked, monsterOids, damage);
   }
}
