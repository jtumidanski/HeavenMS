package portal

import scripting.event.EventManager
import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   EventManager em = pi.getEventManager("KerningTrain")
   if (!em.startInstance(pi.getPlayer())) {
      pi.message("The passenger wagon is already full. Try again a bit later.")
      return false
   }

   pi.playPortalSound()
   return true
}