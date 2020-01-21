package portal


import scripting.portal.PortalPlayerInteraction
import server.maps.MapleMap
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   int jobType = 0
   if (pi.isQuestStarted(20301) || pi.isQuestStarted(20302) || pi.isQuestStarted(20303) || pi.isQuestStarted(20304) || pi.isQuestStarted(20305)) {
      MapleMap map = pi.getClient().getChannelServer().getMapFactory().getMap(108010600 + (10 * jobType))
      if (map.countPlayers() > 0) {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("SOMEONE_ALREADY_SEARCHING"))
         return false
      }

      if (pi.haveItem(4032101 + jobType, 1)) {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("ALREADY_CHALLENGED_MASTER_OF_DISGUISE"))
         return false
      }

      pi.playPortalSound()
      pi.warp(108010600 + (10 * jobType), "out00")
   } else {
      pi.playPortalSound()
      pi.warp(130010010, "out00")
   }
   return true
}