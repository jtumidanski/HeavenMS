package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.isEventLeader()) {
      EventInstanceManager eim = pi.getPlayer().getEventInstance()
      int target
      byte theWay = pi.getMap().getReactorByName("light").getState()
      if (theWay == (byte) 1) {
         target = 240050300 //light
      } else if (theWay == (byte) 3) {
         target = 240050310 //dark
      } else {
         pi.playerMessage(5, "Hit the Lightbulb to determine your fate!")
         return false
      }

      pi.playPortalSound()
      eim.warpEventTeam(target)
      return true
   } else {
      pi.playerMessage(6, "You are not the party leader. Only the party leader may proceed through this portal.")
      return false
   }
}