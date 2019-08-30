package portal


import scripting.portal.PortalPlayerInteraction
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   int jobtype = 4

      if (pi.isQuestStarted(20301) || pi.isQuestStarted(20302) || pi.isQuestStarted(20303) || pi.isQuestStarted(20304) || pi.isQuestStarted(20305)) {
         MapleMap map = pi.getClient().getChannelServer().getMapFactory().getMap(108010600 + (10 * jobtype))
         if (map.countPlayers() > 0) {
            MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Someone else is already searching the area.")
            return false
         }

         if (pi.haveItem(4032101 + jobtype, 1)) {
            MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You have already challenged the Master of Disguise, report your success to the Chief Knight.")
            return false
         }

         pi.playPortalSound(); pi.warp(108010600 + (10 * jobtype), "east00")
      } else {
         pi.playPortalSound(); pi.warp(130020000, "east00")
      }
      return true
   }