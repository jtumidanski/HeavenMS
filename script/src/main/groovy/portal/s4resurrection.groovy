package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if(pi.haveItem(4001108)) {
      if(pi.getWarpMap(923000100).countPlayers() == 0) {
         pi.resetMapObjects(923000100)
         pi.playPortalSound()
         pi.warp(923000100, 0)

         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("OTHER_PLAYER_INSIDE"))
         return false
      }
   }

   MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("MYSTERIOUS_FORCE"))
   return false
}