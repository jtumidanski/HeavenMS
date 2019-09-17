package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if(pi.isQuestStarted(6241) || pi.isQuestStarted(6243)) {
      if(pi.getWarpMap(924000100).countPlayers() == 0) {
         pi.resetMapObjects(924000100)
         pi.playPortalSound()
         pi.warp(924000100, 0)

         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Some other player is currently inside.")
         return false
      }
   }

   MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "A mysterious force won't let you in.")
   return false
}