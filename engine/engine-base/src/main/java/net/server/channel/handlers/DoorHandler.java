package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.DoorPacket;
import net.server.channel.packet.reader.DoorReader;
import server.maps.MapleDoorObject;
import server.maps.MapleMapObject;
import tools.PacketCreator;
import tools.packet.foreigneffect.ShowBlockedMessage;
import tools.packet.stat.EnableActions;

public final class DoorHandler extends AbstractPacketHandler<DoorPacket> {
   @Override
   public Class<DoorReader> getReaderClass() {
      return DoorReader.class;
   }

   @Override
   public void handlePacket(DoorPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      if (chr.isChangingMaps() || chr.isBanned()) {
         PacketCreator.announce(client, new EnableActions());
         return;
      }

      for (MapleMapObject obj : chr.getMap().getMapObjects()) {
         if (obj instanceof MapleDoorObject door) {
            if (door.getOwnerId() == packet.ownerId()) {
               door.warp(chr);
               return;
            }
         }
      }

      PacketCreator.announce(client, new ShowBlockedMessage(6));
      PacketCreator.announce(client, new EnableActions());
   }
}
