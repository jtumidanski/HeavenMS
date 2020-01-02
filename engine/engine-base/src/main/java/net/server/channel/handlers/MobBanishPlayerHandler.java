package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.MobBanishPlayerPacket;
import net.server.channel.packet.reader.MobBanishPlayerReader;
import server.life.BanishInfo;
import server.life.MapleMonster;

public final class MobBanishPlayerHandler extends AbstractPacketHandler<MobBanishPlayerPacket> {
   @Override
   public Class<MobBanishPlayerReader> getReaderClass() {
      return MobBanishPlayerReader.class;
   }

   @Override
   public void handlePacket(MobBanishPlayerPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      MapleMonster mob = chr.getMap().getMonsterById(packet.mobId());

      if (mob != null) {
         BanishInfo banishInfo = mob.getBanish();
         if (banishInfo != null) {
            chr.changeMapBanish(banishInfo.map(), banishInfo.portal(), banishInfo.msg());
         }
      }
   }
}