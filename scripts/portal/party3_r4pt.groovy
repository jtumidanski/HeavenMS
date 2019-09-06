package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   EventInstanceManager eim = pi.getEventInstance()
   if (eim.getProperty("stage4_comb") == null) {
      int r = Math.floor((Math.random() * 3)).intValue() + 1
      int s = Math.floor((Math.random() * 3)).intValue() + 1

      eim.setProperty("stage4_comb", "" + r + s)
   }

   int pname = (pi.getPortal().getName().substring(4, 6)).toInteger()
   int cname = (eim.getProperty("stage4_comb")).toInteger()

   boolean secondPt = true
   if (pi.getPortal().getId() < 14) {
      cname = Math.floor(cname / 10).intValue()
      secondPt = false
   }

   if ((pname % 10) == (cname % 10)) {    //climb
      int nextPortal
      if (secondPt) {
         nextPortal = 1
      } else {
         nextPortal = pi.getPortal().getId() + 3
      }

      pi.playPortalSound(); pi.warp(pi.getMapId(), nextPortal)
   } else {    //fail
      pi.playPortalSound(); pi.warp(pi.getMapId(), 2)
   }

   return true
}