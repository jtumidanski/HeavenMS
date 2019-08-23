package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.showInstruction("Once you leave this area you won't be able to return.", 150, 5)
   return true
}