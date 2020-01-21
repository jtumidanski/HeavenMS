package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if(pi.isQuestStarted(6134)) {
      pi.playPortalSound()
      pi.warp(922020000, 0)
      return true
   }

   MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("MYSTERIOUS_FORCE"))
   return false
}