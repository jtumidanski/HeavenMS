package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.showInstruction("You can move by using the arrow keys.", 250, 5)
   return true
}