package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   pi.playPortalSound()
   pi.warp(926130100, "in02")
   return true
}