package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
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
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("OTHER_PLAYER_INSIDE"))
         return false
      }
   }

   MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("MYSTERIOUS_FORCE"))
   return false
}