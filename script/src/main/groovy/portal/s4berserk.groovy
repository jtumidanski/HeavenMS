package portal


import scripting.portal.PortalPlayerInteraction
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(6153) && pi.haveItem(4031475)) {
      MapleMap map = pi.getWarpMap(910500200)
      if (map.countPlayers() == 0) {
         pi.resetMapObjects(910500200)
         map.shuffleReactors()
         pi.playPortalSound(); pi.warp(910500200, "out01")

         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Some other player is currently inside.")
         return false
      }
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "A mysterious force won't let you in.")
      return false
   }
}