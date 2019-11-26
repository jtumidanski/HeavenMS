package net.server.channel.handlers;

import client.MapleClient;
import client.keybind.MapleQuickSlotBinding;
import net.server.AbstractPacketHandler;
import net.server.PacketReader;
import net.server.channel.packet.ChangeQuickSlotPacket;
import net.server.channel.packet.reader.ChangeQuickSlotReader;

public class ChangeQuickSlotHandler extends AbstractPacketHandler<ChangeQuickSlotPacket> {
   @Override
   public boolean successfulProcess(MapleClient client) {
      return client.getPlayer() != null;
   }

   @Override
   public Class<? extends PacketReader<ChangeQuickSlotPacket>> getReaderClass() {
      return ChangeQuickSlotReader.class;
   }

   @Override
   public void handlePacket(ChangeQuickSlotPacket packet, MapleClient client) {
      client.getPlayer().setQuickSlotBinding(new MapleQuickSlotBinding(packet.keyMap()));
   }
}
