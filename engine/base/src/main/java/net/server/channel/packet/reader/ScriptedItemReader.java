package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.ScriptedItemPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class ScriptedItemReader implements PacketReader<ScriptedItemPacket> {
   @Override
   public ScriptedItemPacket read(SeekableLittleEndianAccessor accessor) {
      int timestamp = accessor.readInt();
      short itemSlot = accessor.readShort();
      int itemId = accessor.readInt();
      return new ScriptedItemPacket(timestamp, itemSlot, itemId);
   }
}
