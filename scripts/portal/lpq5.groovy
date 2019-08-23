package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction
import server.MaplePortal
import server.maps.MapleMap

static def enter(PortalPlayerInteraction pi) {
   int nextMap = 922010700
   EventInstanceManager eim = pi.getPlayer().getEventInstance()
   MapleMap target = eim.getMapInstance(nextMap)
   MaplePortal targetPortal = target.getPortal("st00")
   // only let people through if the eim is ready
   String avail = eim.getProperty("5stageclear")
   if (avail == null) {
      // can't go thru eh?
      pi.getPlayer().dropMessage(5, "Some seal is blocking this door.")
      return false
   } else {
      if (eim.getProperty("6stageclear") == null) {
         eim.setProperty("6stageclear", "true")
      }
      pi.playPortalSound()
      pi.getPlayer().changeMap(target, targetPortal)
      return true
   }
}