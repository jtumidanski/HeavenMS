package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   EventInstanceManager eim = pi.getPlayer().getEventInstance()
   int fc = eim.getIntProperty("falls")

   if (fc >= 3) {
      pi.playPortalSound()
      pi.warp(922240200, 0)
   } else {
      eim.setIntProperty("falls", fc + 1)
      pi.playPortalSound()
      pi.warp(pi.getPlayer().getMapId(), 0)
   }

   return true
}