package net.server.handlers;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.packet.CustomPacket;
import net.server.packet.reader.CustomReader;
import tools.PacketCreator;

public class CustomPacketHandler extends AbstractPacketHandler<CustomPacket> {
   @Override
   public Class<CustomReader> getReaderClass() {
      return CustomReader.class;
   }

   @Override
   public void handlePacket(CustomPacket packet, MapleClient client) {
      if (packet.bytes().length > 0 && client.getGMLevel() == 4) {
         client.announce(PacketCreator.customPacket(packet.bytes()));
      }
   }

   @Override
   public boolean validateState(MapleClient client) {
      return true;
   }
}
