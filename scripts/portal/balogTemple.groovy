package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.playPortalSound(); pi.warp(105100000, 2)
   return true
}