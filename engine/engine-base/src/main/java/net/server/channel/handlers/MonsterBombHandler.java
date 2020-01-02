package net.server.channel.handlers;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.MonsterBombPacket;
import net.server.channel.packet.reader.MonsterBombReader;
import server.life.MapleMonster;
import tools.MasterBroadcaster;
import tools.packet.monster.KillMonster;

public final class MonsterBombHandler extends AbstractPacketHandler<MonsterBombPacket> {
   @Override
   public Class<MonsterBombReader> getReaderClass() {
      return MonsterBombReader.class;
   }

   @Override
   public void handlePacket(MonsterBombPacket packet, MapleClient client) {
      MapleMonster monster = client.getPlayer().getMap().getMonsterByOid(packet.objectId());
      if (!client.getPlayer().isAlive() || monster == null) {
         return;
      }
      if (monster.id() == 8500003 || monster.id() == 8500004) {
         MasterBroadcaster.getInstance().sendToAllInMap(monster.getMap(), new KillMonster(monster.objectId(), 4));
         client.getPlayer().getMap().removeMapObject(packet.objectId());
      }
   }
}
