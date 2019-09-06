package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.PlayerLoggedInPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class PlayerLoggedInReader implements PacketReader<PlayerLoggedInPacket> {
   @Override
   public PlayerLoggedInPacket read(SeekableLittleEndianAccessor accessor) {
      final int cid = accessor.readInt();
      return new PlayerLoggedInPacket(cid);
   }
}
