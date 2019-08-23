package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getMap().getReactorByName("stonegate").getState() == (byte) 1) {
      pi.playPortalSound()
      pi.warp(990000430, 0)
      return true
   } else {
      pi.getPlayer().dropMessage(5, "The door is still blocked.")
      return false
   }
}