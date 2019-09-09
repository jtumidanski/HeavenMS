package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   EventInstanceManager eim = pi.getEventInstance()
   int level = eim.getProperty("level").toInteger()
   if(eim.getProperty("stage3b") == "0") {
      pi.getMap(925100302).spawnAllMonstersFromMapSpawnList(level, true)
      eim.setProperty("stage3b", "1")
   }

   pi.playPortalSound(); pi.warp(925100302,0)
   return true
}