package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (!pi.getEventInstance().isEventCleared()) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("ZAKUM_COMPLETE_TRIALS"))
      return false
   }

   if (pi.getEventInstance().gridCheck(pi.getPlayer()) == -1) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("ZAKUM_CLAIM_PRIZE"))
      return false
   }

   pi.playPortalSound()
   pi.warp(211042300)
   return true
}