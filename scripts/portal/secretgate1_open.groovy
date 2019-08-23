package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getMap().getReactorByName("secretgate1").getState() == (byte) 1) {
      pi.playPortalSound()
      pi.warp(990000611,1)
      return true
   } else {
      pi.playerMessage(5, "This door is closed.")
      return false
   }
}