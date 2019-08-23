package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getMap().getReactorByName("watergate").getState() == (byte) 1) {
      pi.playPortalSound()
      pi.warp(990000600, 1)
      return true
   } else
      pi.getPlayer().dropMessage(5, "This way forward is not open yet.")
   return false
}