package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.playPortalSound()
   pi.warp(920010600, Math.random() * 3 > 1 ? 1 : 2)
   return true
}