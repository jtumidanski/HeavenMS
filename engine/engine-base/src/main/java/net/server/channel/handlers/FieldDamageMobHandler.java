package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import constants.game.GameConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.FieldDamageMobPacket;
import net.server.channel.packet.reader.FieldDamageMobReader;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.maps.MapleMap;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.MasterBroadcaster;
import tools.packet.monster.DamageMonster;

public class FieldDamageMobHandler extends AbstractPacketHandler<FieldDamageMobPacket> {
   @Override
   public Class<FieldDamageMobReader> getReaderClass() {
      return FieldDamageMobReader.class;
   }

   @Override
   public void handlePacket(FieldDamageMobPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      MapleMap map = chr.getMap();

      if (map.getEnvironment().isEmpty()) {   // no environment objects activated to actually hit the mob
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS, client.getPlayer().getName() + " tried to use an obstacle on map id " + map.getId() + " to attack.");
         return;
      }

      MapleMonster mob = map.getMonsterByOid(packet.mobId());
      if (mob != null) {

         if (packet.damage() < 0 || packet.damage() > GameConstants.MAX_FIELD_MOB_DAMAGE) {
            MasterBroadcaster.getInstance().sendToAllInMap(map, new DamageMonster(packet.mobId(), packet.damage()), true, chr);
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS, client.getPlayer().getName() + " tried to use an obstacle on map id " + map.getId() + " to attack " + MapleMonsterInformationProvider.getInstance().getMobNameFromId(mob.id()) + " with damage " + packet.damage());
            return;
         }
      }
      map.damageMonster(chr, mob, packet.damage());
   }
}
