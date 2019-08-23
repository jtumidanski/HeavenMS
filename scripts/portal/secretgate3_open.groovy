package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getMap().getReactorByName("secretgate3").getState() == (byte) 1) {
      pi.playPortalSound()
      pi.warp(990000641,1)
      return true
   } else {
      pi.getPlayer().dropMessage(5, "This door is closed.")
      return false
   }
}