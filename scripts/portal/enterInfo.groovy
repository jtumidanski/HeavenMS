package portal

import scripting.portal.PortalPlayerInteraction
import server.life.MapleLifeFactory
import server.maps.MapleMap

import java.awt.*

static def enter(PortalPlayerInteraction pi) {
   MapleMap mapobj = pi.getWarpMap(104000004)
   if (pi.isQuestActive(21733) && pi.getQuestProgress(21733, 9300345) == 0 && mapobj.countMonsters() == 0) {
      mapobj.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300345), new Point(0, 0))
   }

   pi.playPortalSound()
   pi.warp(104000004, 1)
   return true
}