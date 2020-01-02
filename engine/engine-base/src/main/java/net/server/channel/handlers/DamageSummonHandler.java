package net.server.channel.handlers;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.DamageSummonPacket;
import net.server.channel.packet.reader.DamageSummonReader;
import server.maps.MapleMapObject;
import server.maps.MapleSummon;
import tools.MasterBroadcaster;
import tools.packet.monster.DamageSummon;

public final class DamageSummonHandler extends AbstractPacketHandler<DamageSummonPacket> {
   @Override
   public Class<DamageSummonReader> getReaderClass() {
      return DamageSummonReader.class;
   }

   @Override
   public void handlePacket(DamageSummonPacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      MapleMapObject mmo = player.getMap().getMapObject(packet.objectId());

      if (mmo instanceof MapleSummon) {
         MapleSummon summon = (MapleSummon) mmo;

         summon.addHP(-packet.damage());
         if (summon.getHP() <= 0) {
            player.cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
         }
         MasterBroadcaster.getInstance().sendToAllInMapRange(player.getMap(), new DamageSummon(player.getId(), packet.objectId(), packet.damage(), packet.monsterIdFrom()), player, summon.position());
      }
   }
}
