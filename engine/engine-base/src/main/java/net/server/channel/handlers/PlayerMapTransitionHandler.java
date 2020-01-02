package net.server.channel.handlers;

import java.util.Collections;
import java.util.List;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import server.life.MapleMonster;
import server.maps.MapleMapObject;
import server.processor.maps.MapleMapObjectProcessor;
import tools.PacketCreator;
import tools.Pair;
import tools.packet.buff.GiveBuff;
import tools.packet.spawn.StopMonsterControl;

public final class PlayerMapTransitionHandler extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      chr.setMapTransitionComplete();

      int beaconId = chr.getBuffSource(MapleBuffStat.HOMING_BEACON);
      if (beaconId != -1) {
         chr.cancelBuffStats(MapleBuffStat.HOMING_BEACON);

         final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.HOMING_BEACON, 0));
         PacketCreator.announce(chr, new GiveBuff(1, beaconId, stat));
      }

      if (!chr.isHidden()) {
         for (MapleMapObject mo : chr.getMap().getMonsters()) {
            MapleMonster m = (MapleMonster) mo;
            if (m.getSpawnEffect() == 0 || m.getHp() < m.getMaxHp()) {     // avoid effect-spawning mobs
               if (m.getController() == chr) {
                  PacketCreator.announce(client, new StopMonsterControl(m.objectId()));
                  MapleMapObjectProcessor.getInstance().sendDestroyData(m, client);
                  m.aggroRedirectController();
               } else {
                  MapleMapObjectProcessor.getInstance().sendDestroyData(m, client);
               }

               m.aggroSwitchController(chr, false);
               MapleMapObjectProcessor.getInstance().sendSpawnData(m, client);
            }
         }
      }
   }
}