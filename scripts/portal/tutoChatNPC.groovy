package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.hasLevel30Character()) {
      pi.openNpc(2007)
   }
   pi.blockPortal()
   return true
}