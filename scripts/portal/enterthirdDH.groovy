package portal


import scripting.portal.PortalPlayerInteraction
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.hasItem(4032120) || pi.hasItem(4032121) || pi.hasItem(4032122) || pi.hasItem(4032123) || pi.hasItem(4032124)) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You already have the proof of qualification.")
      return false
   }
   if (pi.isQuestStarted(20601) || pi.isQuestStarted(20602) || pi.isQuestStarted(20603) || pi.isQuestStarted(20604) || pi.isQuestStarted(20605)) {
      if (pi.getPlayerCount(913010200) == 0) {
         MapleMap map = pi.getMap(913010200)
         map.killAllMonsters()
         pi.playPortalSound(); pi.warp(913010200, 0)
         pi.spawnMonster(9300289, 0, 0)
         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Someone is already attempting to defeat the boss. Better come back later.")
         return false
      }
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The only way to enter the hall #3 is if you're training for the Level 100 skills.")
      return false
   }
}