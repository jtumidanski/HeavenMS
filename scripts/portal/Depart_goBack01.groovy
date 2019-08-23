package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.playPortalSound()
   pi.warp(pi.getPlayer().getMap().getId() - 10, "left01")
   return true
}