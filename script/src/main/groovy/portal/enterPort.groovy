package portal


import scripting.portal.PortalPlayerInteraction
import server.life.MapleLifeFactory
import server.life.MapleMonster
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType

import java.awt.Point

boolean enter(PortalPlayerInteraction pi) {
   if(pi.isQuestStarted(21301) && pi.getQuestProgressInt(21301, 9001013) == 0) {
      if(pi.getPlayerCount(108010700) != 0) {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The portal is blocked from the other side. I wonder if someone is already fighting the Thief Crow?")
         return false
      } else {
         MapleMap map = pi.getClient().getChannelServer().getMapFactory().getMap(108010700)
         spawnMob(2732, 3, 9001013, map)

         pi.playPortalSound()
         pi.warp(108010700, "west00")
      }
   } else {
      pi.playPortalSound(); pi.warp(140020300, 1)
   }
   return true
}

static def spawnMob(x, y, int id, MapleMap map) {
   if(map.getMonsterById(id) != null)
      return

   MapleMonster mob = MapleLifeFactory.getMonster(id)
   map.spawnMonsterOnGroundBelow(mob, new Point(x, y))
}