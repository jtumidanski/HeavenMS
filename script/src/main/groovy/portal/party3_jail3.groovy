package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if(pi.getEventInstance().getIntProperty("statusStg8") == 1) {
      pi.playPortalSound()
      pi.warp(920010930,0)
      return true
   }
   else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("PIXIE_POWER_REMAINS"))
      return false
   }
}