package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   try {
      EventInstanceManager eim = pi.getEventInstance()
      if (eim != null && eim.getProperty("stage2") == "3") {
         pi.playPortalSound(); pi.warp(925100200, 0) //next
         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The portal is not opened yet.")
         return false
      }
   } catch (e) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Error: " + e)
   }

   return false
}