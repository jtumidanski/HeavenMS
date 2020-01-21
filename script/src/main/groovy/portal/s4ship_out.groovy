package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   int exit = pi.getEventInstance().getIntProperty("canLeave")
   if (exit == 0) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("WAIT_TO_LEAVE"))
      return false
   } else if (exit == 2) {
      pi.playPortalSound()
      pi.warp(912010200)
      return true
   } else {
      pi.playPortalSound()
      pi.warp(120000101)
      return true
   }
}