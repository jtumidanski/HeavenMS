package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   pi.removeGuide()
   pi.blockPortal()
   return true
}