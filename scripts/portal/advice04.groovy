package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.showInstruction("Click \r\\#b<Sera>", 100, 5)
   return true
}