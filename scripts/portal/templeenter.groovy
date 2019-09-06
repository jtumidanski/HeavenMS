package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   pi.cancelItem(2210016)
   pi.playPortalSound()
   pi.warp(270000100, "out00")
   return true
}