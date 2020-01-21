package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
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
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("OTHER_PLAYER_INSIDE"))
         return false
      }
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("MYSTERIOUS_FORCE"))
      return false
   }
}