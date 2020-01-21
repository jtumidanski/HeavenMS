package portal


import scripting.portal.PortalPlayerInteraction
import server.maps.MapleMap
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.hasItem(4032120) || pi.hasItem(4032121) || pi.hasItem(4032122) || pi.hasItem(4032123) || pi.hasItem(4032124)) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("ALREADY_HAVE_PROOF_OF_QUALIFICATION"))
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
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("SOMEONE_ALREADY_ATTEMPTING_BOSS"))
         return false
      }
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("LEVEL_100_SKILL_REQUIREMENT"))
      return false
   }
}