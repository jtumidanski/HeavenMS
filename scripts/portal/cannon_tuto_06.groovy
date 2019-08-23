package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   //TODO
   // pi.setDirectionStatus(true)
   pi.lockUI()
   pi.openNpc(3, "1096003")
   return true
}