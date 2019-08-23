package portal


import scripting.portal.PortalPlayerInteraction
import server.maps.MapleReactor

static def enter(PortalPlayerInteraction pi) {
   String name = pi.getPortal().getName().substring(2, 4)
   MapleReactor gate = pi.getPlayer().getMap().getReactorByName("gate" + name)
   if (gate != null && gate.getState() == (byte) 4) {
      pi.playPortalSound(); pi.warp(670010600, "gt" + name + "PIB")
      return true
   } else {
      pi.message("The gate is not opened yet.")
      return false
   }
}