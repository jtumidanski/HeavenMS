package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   pi.playPortalSound()
   pi.warp(pi.getPlayer().getSavedLocation("EVENT"))
   return true
}