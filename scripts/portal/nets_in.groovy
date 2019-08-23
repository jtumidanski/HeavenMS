package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.getPlayer().saveLocation("MIRROR")
   pi.playPortalSound()
   pi.warp(926010000, 4)
   return true
}