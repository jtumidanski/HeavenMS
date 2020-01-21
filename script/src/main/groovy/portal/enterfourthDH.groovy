package portal


import scripting.portal.PortalPlayerInteraction
import server.maps.MapleMap
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.hasItem(4032125) || pi.hasItem(4032126) || pi.hasItem(4032127) || pi.hasItem(4032128) || pi.hasItem(4032129)) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("ALREADY_HAVE_PROOF"))
      return false
   }

   if (pi.isQuestStarted(20611) || pi.isQuestStarted(20612) || pi.isQuestStarted(20613) || pi.isQuestStarted(20614) || pi.isQuestStarted(20615)) {
      if (pi.getPlayerCount(913020300) == 0) {
         MapleMap map = pi.getMap(913020300)
         map.killAllMonsters()

         pi.playPortalSound()
         pi.warp(913020300, 0)
         pi.spawnMonster(9300294, 87, 88)
         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("SOMEONE_ALREADY_ATTEMPTING_BOSS"))
         return false
      }
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("CANNOT_ACCESS_HALL"))
      return false
   }
}