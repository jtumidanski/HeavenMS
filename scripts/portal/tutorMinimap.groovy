package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.guideHint(1)
   pi.blockPortal()
   return true
}