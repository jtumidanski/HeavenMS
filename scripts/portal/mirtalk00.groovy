package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.blockPortal()
   if (pi.containsAreaInfo((short) 22013, "dt00=o")) {
      return false
   }
   pi.mapEffect("evan/dragonTalk00")
   pi.updateAreaInfo((short) 22013, "dt00=o;mo00=o")
   return true
}