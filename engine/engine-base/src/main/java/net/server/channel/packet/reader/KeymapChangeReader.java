package net.server.channel.packet.reader;

import java.util.stream.IntStream;

import net.server.PacketReader;
import net.server.channel.packet.keymap.AutoHPKeymapChangePacket;
import net.server.channel.packet.keymap.AutoMPKeymapChangePacket;
import net.server.channel.packet.keymap.BaseKeymapChangePacket;
import net.server.channel.packet.keymap.KeyTypeAction;
import net.server.channel.packet.keymap.RegularKeymapChangePacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class KeymapChangeReader implements PacketReader<BaseKeymapChangePacket> {
   @Override
   public BaseKeymapChangePacket read(SeekableLittleEndianAccessor accessor) {
      boolean available = accessor.available() >= 8;
      int mode = -1;
      if (available) {
         mode = accessor.readInt();
         if (mode == 0) {
            int numChanges = accessor.readInt();
            KeyTypeAction[] changes = IntStream.generate(() -> 1).limit(numChanges).mapToObj(id -> new KeyTypeAction(accessor.readInt(), accessor.readByte(), accessor.readInt())).toArray(KeyTypeAction[]::new);
            return new RegularKeymapChangePacket(available, mode, changes);
         } else if (mode == 1) {
            int itemId = accessor.readInt();
            return new AutoHPKeymapChangePacket(available, mode, itemId);
         } else if (mode == 2) {
            int itemId = accessor.readInt();
            return new AutoMPKeymapChangePacket(available, mode, itemId);
         }
      }
      return new BaseKeymapChangePacket(available, mode);
   }
}
