package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   pi.guideHint(1)
   pi.blockPortal()
   return true
}