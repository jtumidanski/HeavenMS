package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.MobDamageMobFriendlyPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class MobDamageMobFriendlyReader implements PacketReader<MobDamageMobFriendlyPacket> {
   @Override
   public MobDamageMobFriendlyPacket read(SeekableLittleEndianAccessor accessor) {
      int attacker = accessor.readInt();
      accessor.readInt();
      int damaged = accessor.readInt();
      return new MobDamageMobFriendlyPacket(attacker, damaged);
   }
}
