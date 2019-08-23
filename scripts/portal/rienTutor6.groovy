package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.removeGuide()
   pi.blockPortal()
   return true
}