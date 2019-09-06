package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(6240)) {
      if (pi.getWarpMap(921100200).countPlayers() == 0) {
         pi.resetMapObjects(921100200)
         pi.playPortalSound()
         pi.warp(921100200, 0)

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