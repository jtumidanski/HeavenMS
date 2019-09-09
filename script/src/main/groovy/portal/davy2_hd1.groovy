package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   EventInstanceManager eim = pi.getEventInstance()
   int level = eim.getProperty("level").toInteger()
   if (eim.getProperty("stage2b") == "0") {
      pi.getMap(925100202).spawnAllMonstersFromMapSpawnList(level, true)
      eim.setProperty("stage2b", "1")
   }

   pi.playPortalSound(); pi.warp(925100202, 0)
   return true
}