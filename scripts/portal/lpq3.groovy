package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction
import server.maps.MaplePortal
import server.maps.MapleMap

static def enter(PortalPlayerInteraction pi) {
   int nextMap = 922010500
   EventInstanceManager eim = pi.getPlayer().getEventInstance()
   MapleMap target = eim.getMapInstance(nextMap)
   MaplePortal targetPortal = target.getPortal("st00")
   // only let people through if the eim is ready
   String avail = eim.getProperty("4stageclear")
   if (avail == null) {
      // can't go thru eh?
      pi.getPlayer().dropMessage(5, "Some seal is blocking this door.")
      return false
   } else {
      pi.playPortalSound()
      pi.getPlayer().changeMap(target, targetPortal)
      return true
   }
}