package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   pi.showInstruction("You can move by using the arrow keys.", 250, 5)
   return true
}