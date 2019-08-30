package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   EventInstanceManager eim = pi.getEventInstance()
   if (eim != null) {
      if (eim.getIntProperty("glpq6") < 3) {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The portal is not opened yet.")
         return false
      } else {
         pi.playPortalSound()
         pi.warp(610030700, 0)
         return true
      }
   }

   return false
}