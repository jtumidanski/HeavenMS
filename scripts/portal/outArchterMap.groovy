package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.playPortalSound()
   pi.warp(100000000, "Achter00")
   pi.playPortalSound()
   return true
}