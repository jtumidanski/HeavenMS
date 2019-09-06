package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   pi.warp(980000201, 0)
   pi.playPortalSound()
   return true
}