package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.warp(980000501, 0)
   pi.playPortalSound()
   return true
}