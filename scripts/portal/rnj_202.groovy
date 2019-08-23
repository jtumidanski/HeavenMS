package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.getMap().getReactorByName("rnj32_out").getState() == (byte) 1) {
      pi.playPortalSound()
      pi.warp(926100200, 2)
      return true
   } else {
      pi.playerMessage(5, "The door is not opened yet.")
      return false
   }
}