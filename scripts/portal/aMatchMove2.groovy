package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.playPortalSound(); pi.warp(pi.getPlayer().getSavedLocation("MIRROR"))
   return true
}