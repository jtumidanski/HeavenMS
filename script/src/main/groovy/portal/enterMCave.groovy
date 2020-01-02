package portal

import scripting.portal.PortalPlayerInteraction
import server.life.MapleLifeFactory
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType

import java.awt.*

boolean enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(21201)) { // Second Job
      for (int i = 108000700; i < 108000709; i++) {
         if (pi.getPlayerCount(i) > 0 && pi.getPlayerCount(i + 10) > 0) {
            continue
         }

         pi.playPortalSound()
         pi.warp(i, "out00")
         pi.setQuestProgress(21202, 21203, 0)
         return true
      }
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The mirror is blank due to many players recalling their memories. Please wait and try again.")
      return false
   } else if (pi.isQuestStarted(21302) && !pi.isQuestCompleted(21303)) { // Third Job
      if (pi.getPlayerCount(108010701) > 0 || pi.getPlayerCount(108010702) > 0) {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The mirror is blank due to many players recalling their memories. Please wait and try again.")
         return false
      } else {
         MapleMap map = pi.getClient().getChannelServer().getMapFactory().getMap(108010702)
         spawnMob(-210, 454, 9001013, map)

         pi.playPortalSound()
         pi.setQuestProgress(21303, 21203, 1)
         pi.warp(108010701, "out00")
         return true
      }
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You have already passed your test, there is no need to access the mirror again.")
      return false
   }
}

static def spawnMob(x, y, int id, MapleMap map) {
   if (map.getMonsterById(id) != null) {
      return
   }

   MapleLifeFactory.getMonster(id).ifPresent({ mob -> map.spawnMonsterOnGroundBelow(mob, new Point(x, y)) })
}