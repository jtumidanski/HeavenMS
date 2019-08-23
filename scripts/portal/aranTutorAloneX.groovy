package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.playPortalSound(); pi.warp(914000100, 1)
   return true
}