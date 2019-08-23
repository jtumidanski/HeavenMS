package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.getMap().getReactorByName("jnr6_out").getState() == (byte) 1) {
      pi.playPortalSound()
      pi.warp(926110300)
      return true
   } else {
      pi.playerMessage(5, "The portal is not opened yet.")
      return false
   }
}