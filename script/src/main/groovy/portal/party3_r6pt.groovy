package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   EventInstanceManager eim = pi.getEventInstance()
   if (eim.getProperty("stage6_comb") == null) {
      String comb = "0"

      for (int i = 0; i < 16; i++) {
         int r = Math.floor((Math.random() * 4)).intValue() + 1
         comb += r.toString()
      }

      eim.setProperty("stage6_comb", comb)
   }

   String comb = eim.getProperty("stage6_comb")

   String name = pi.getPortal().getName().substring(2, 5)
   int portalId = (name).toInteger()


   int pRow = Math.floor(portalId / 10).toInteger()
   int pCol = portalId % 10

   if (pCol == (comb.substring(pRow, pRow + 1)).toInteger()) {    //climb
      pi.playPortalSound(); pi.warp(pi.getMapId(), (pRow % 4 != 0) ? pi.getPortal().getId() + 4 : (pRow / 4).toInteger())
   } else {    //fail
      pRow--
      pi.playPortalSound(); pi.warp(pi.getMapId(), (pRow / 4) > 1 ? (pRow / 4).toInteger() : 5)
   }

   return true
}