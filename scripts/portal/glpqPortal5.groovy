package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   EventInstanceManager eim = pi.getEventInstance()
   if (eim != null) {
      if (eim.getIntProperty("glpq5") < 5) {
         pi.playerMessage(5, "The portal is not opened yet.")
         return false
      } else {
         pi.playPortalSound()
         pi.warp(610030600, 0)
         return true
      }
   }

   return false
}