package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   String mapPlayer = "stage6_comb" + (pi.getMapId() % 10)
   EventInstanceManager eim = pi.getEventInstance()

   if (eim.getProperty(mapPlayer) == null) {
      String comb = ""

      for (int i = 0; i < 10; i++) {
         int r = Math.floor((Math.random() * 4)).intValue()
         comb += r.toString()
      }

      eim.setProperty(mapPlayer, comb)
   }

   String comb = eim.getProperty(mapPlayer)

   String name = pi.getPortal().getName().substring(2, 4)
   int portalId = (name).toInteger()


   int pRow = Math.floor(portalId / 10).toInteger()
   int pCol = (portalId % 10)

   if (pCol == (comb.substring(pRow, pRow + 1)).toInteger()) {    //climb
      if (pRow < 9) {
         pi.playPortalSound(); pi.warp(pi.getMapId(), pi.getPortal().getId() + 4)
      } else {
         if (eim.getIntProperty("statusStg6") == 0) {
            eim.setIntProperty("statusStg6", 1)
            eim.giveEventPlayersStageReward(6)
         }

         pi.playPortalSound(); pi.warp(pi.getMapId(), 1)
      }

   } else {    //fail
      pi.playPortalSound(); pi.warp(pi.getMapId(), 2)
   }

   return true
}