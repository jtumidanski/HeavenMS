package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   try {
      EventInstanceManager eim = pi.getEventInstance()
      if (eim != null && eim.getProperty("stage2") == "3") {
         pi.playPortalSound(); pi.warp(925100200, 0) //next
         return true
      } else {
         pi.playerMessage(5, "The portal is not opened yet.")
         return false
      }
   } catch (e) {
      pi.playerMessage(5, "Error: " + e)
   }

   return false
}