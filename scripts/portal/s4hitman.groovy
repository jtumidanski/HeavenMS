package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(6201)) {
      if (pi.getWarpMap(910200000).countPlayers() == 0) {
         pi.resetMapObjects(910200000)
         pi.playPortalSound()
         pi.warp(910200000, 0)

         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Some other player is currently inside.")
         return false
      }
   }

   MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "A mysterious force won't let you in.")
   return false
}