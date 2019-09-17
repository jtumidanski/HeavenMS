package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   pi.playPortalSound()
   pi.warp(922240100 + (pi.getPlayer().getMapId() - 922240000), 0)
   return true
}