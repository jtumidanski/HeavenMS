package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.family.FamilyUsePacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class FamilyUseReader implements PacketReader<FamilyUsePacket> {
   @Override
   public FamilyUsePacket read(SeekableLittleEndianAccessor accessor) {
      int entitlementId = accessor.readInt();
      String characterName = accessor.readMapleAsciiString();
      return new FamilyUsePacket(entitlementId, characterName);
   }
}
