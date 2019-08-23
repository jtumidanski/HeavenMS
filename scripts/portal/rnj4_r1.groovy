package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   EventInstanceManager eim = pi.getEventInstance()
   int area = eim.getIntProperty("statusStg5")
   int reg = 0

   if((area >> reg) % 2 == 0) {
      area |= (1 << reg)
      eim.setIntProperty("statusStg5", area)

      pi.playPortalSound()
      pi.warp(926100301 + reg, 0) //next
      return true
   } else {
      pi.playerMessage(5, "This room is already being explored.")
      return false
   }
}