package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType
import tools.SimpleMessage

boolean enter(PortalPlayerInteraction pi) {
   try {
      EventInstanceManager eim = pi.getEventInstance()
      if (eim != null && eim.getProperty("stage2") == "3") {
         pi.playPortalSound(); pi.warp(925100200, 0) //next
         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("PORTAL_NOT_YET_OPENED"))
         return false
      }
   } catch (e) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, SimpleMessage.from("Error: " + e))
   }

   return false
}