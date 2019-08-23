package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.getMap().getReactorByName("jnr3_out1").getState() == (byte) 1) {
      pi.playPortalSound()
      pi.warp(926110201, 0)
      return true
   } else {
      pi.playerMessage(5, "The door is not opened yet.")
      return false
   }
}