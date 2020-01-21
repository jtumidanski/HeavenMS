package portal


import scripting.portal.PortalPlayerInteraction
import server.maps.MapleMap
import tools.I18nMessage
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
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("OTHER_PLAYER_INSIDE"))
         return false
      }
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("MYSTERIOUS_FORCE"))
      return false
   }
}