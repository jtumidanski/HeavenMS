package portal


import scripting.portal.PortalPlayerInteraction
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   int jobType = 2

   if (pi.isQuestStarted(20301) || pi.isQuestStarted(20302) || pi.isQuestStarted(20303) || pi.isQuestStarted(20304) || pi.isQuestStarted(20305)) {
      MapleMap map = pi.getClient().getChannelServer().getMapFactory().getMap(108010600 + (10 * jobType))
      if (map.countPlayers() > 0) {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Someone else is already searching the area.")
         return false
      }

      if (pi.haveItem(4032101 + jobType, 1)) {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You have already challenged the Master of Disguise, report your success to the Chief Knight.")
         return false
      }

      pi.playPortalSound(); pi.warp(108010600 + (10 * jobType), "out00")
   } else {
      pi.playPortalSound(); pi.warp(130010110, "out00")
   }
   return true
}