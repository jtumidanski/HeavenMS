package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   pi.playPortalSound(); pi.warp(670010400, 0)
   return true
}