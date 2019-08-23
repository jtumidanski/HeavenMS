package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction
import server.maps.MapleMap

static def enter(PortalPlayerInteraction pi) {
   EventInstanceManager eim = pi.getPlayer().getEventInstance()
   MapleMap target = eim.getMapInstance(103000804)
   if (eim.getProperty("4stageclear") != null) {
      pi.playPortalSound()
      pi.getPlayer().changeMap(target, target.getPortal("st00"))
      return true
   }
   else {
      pi.getPlayer().dropMessage(5, "The portal is not opened yet.")
      return false
   }
}