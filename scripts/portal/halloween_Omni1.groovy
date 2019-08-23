package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.playerMessage(5, "It seems to be locked.")
   return true
}