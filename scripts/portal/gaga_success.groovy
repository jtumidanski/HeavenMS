package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.playPortalSound()
   pi.warp(922240100 + (pi.getPlayer().getMapId() - 922240000))
   return true
}