package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if(pi.haveItem(4001108)) {
      if(pi.getWarpMap(923000100).countPlayers() == 0) {
         pi.resetMapObjects(923000100)
         pi.playPortalSound()
         pi.warp(923000100, 0)

         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Some other player is currently inside.")
         return false
      }
   }

   MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "A mysterious force won't let you in.")
   return false
}