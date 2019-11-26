package net.server.channel.packet.reader;

import client.keybind.MapleQuickSlotBinding;
import net.server.PacketReader;
import net.server.channel.packet.ChangeQuickSlotPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class ChangeQuickSlotReader implements PacketReader<ChangeQuickSlotPacket> {
   @Override
   public ChangeQuickSlotPacket read(SeekableLittleEndianAccessor accessor) {
      if (accessor.available() != MapleQuickSlotBinding.QUICK_SLOT_SIZE * Integer.BYTES) {
         return null;
      }

      byte[] keyMap = new byte[MapleQuickSlotBinding.QUICK_SLOT_SIZE];
      for (int i = 0; i < MapleQuickSlotBinding.QUICK_SLOT_SIZE; i++) {
         keyMap[i] = (byte) accessor.readInt();
      }
      return new ChangeQuickSlotPacket(keyMap);
   }
}
