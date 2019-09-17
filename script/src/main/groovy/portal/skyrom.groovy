package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if(pi.isQuestStarted(3935) && !pi.haveItem(4031574, 1)) {
      if(pi.getWarpMap(926000010).countPlayers() == 0) {
         pi.playPortalSound()
         pi.warp(926000010, 0)
         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Someone is already trying this map.")
         return false
      }
   } else {
      return false
   }
}