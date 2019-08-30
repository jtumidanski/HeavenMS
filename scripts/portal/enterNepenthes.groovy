package portal

import scripting.portal.PortalPlayerInteraction
import server.life.MapleLifeFactory
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType

import java.awt.*

static def enter(PortalPlayerInteraction pi) {
   if (pi.isQuestActive(21739)) {
      MapleMap mapobj1 = pi.getWarpMap(920030000)
      MapleMap mapobj2 = pi.getWarpMap(920030001)

      if (mapobj1.countPlayers() == 0 && mapobj2.countPlayers() == 0) {
         mapobj1.resetPQ(1)
         mapobj2.resetPQ(1)

         mapobj2.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300348), new Point(591, -34))

         pi.playPortalSound(); pi.warp(920030000, 2)
         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Someone is already challenging the area.")
         return false
      }
   } else {
      pi.playPortalSound(); pi.warp(200060001, 2)
      return true
   }
}