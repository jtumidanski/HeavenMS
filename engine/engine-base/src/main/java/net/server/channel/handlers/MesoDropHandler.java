package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.MesoDropPacket;
import net.server.channel.packet.reader.MesoDropReader;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;

public final class MesoDropHandler extends AbstractPacketHandler<MesoDropPacket> {
   @Override
   public boolean successfulProcess(MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (!player.isAlive()) {
         PacketCreator.announce(client, new EnableActions());
         return false;
      }
      return true;
   }

   @Override
   public Class<MesoDropReader> getReaderClass() {
      return MesoDropReader.class;
   }

   @Override
   public void handlePacket(MesoDropPacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (client.tryAcquireClient()) {
         try {
            if (packet.meso() <= player.getMeso() && packet.meso() > 9 && packet.meso() < 50001) {
               player.gainMeso(-packet.meso(), false, true, false);
            } else {
               PacketCreator.announce(client, new EnableActions());
               return;
            }
         } finally {
            client.releaseClient();
         }
      } else {
         PacketCreator.announce(client, new EnableActions());
         return;
      }

      if (player.attemptCatchFish(packet.meso())) {
         player.getMap().disappearingMesoDrop(packet.meso(), player, player, player.position());
      } else {
         player.getMap().spawnMesoDrop(packet.meso(), player.position(), player, player, true, (byte) 2);
      }
   }
}