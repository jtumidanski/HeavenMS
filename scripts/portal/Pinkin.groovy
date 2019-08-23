package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.playPortalSound()
   pi.warp(270050100)
   return true
}