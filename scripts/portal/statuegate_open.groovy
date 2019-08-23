package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getMap().getReactorByName("statuegate").getState() == (byte) 1) {
      pi.playPortalSound()
      pi.warp(990000301, 0)
      return true
   } else {
      pi.getPlayer().dropMessage(5, "The gate is closed.")
      return false
   }
}