package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.playPortalSound()
   pi.warp(920010500, 3)
   return true
}