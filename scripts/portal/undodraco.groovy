package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.cancelItem(2210016)
   pi.playPortalSound()
   pi.warp(240000110, 2)
   return true
}