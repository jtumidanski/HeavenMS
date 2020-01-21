package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   long jailedTime = pi.getJailTimeLeft()

   if (jailedTime <= 0) {
      pi.playPortalSound(); pi.warp(300000010, "in01")
      return true
   } else {
      int seconds = Math.floor(jailedTime / 1000) % 60
      int minutes = (Math.floor(jailedTime / (1000 * 60)) % 60)
      int hours = (Math.floor(jailedTime / (1000 * 60 * 60)) % 24)

      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("JAIL_NOTE").with(hours, minutes, seconds))
      return false
   }
}