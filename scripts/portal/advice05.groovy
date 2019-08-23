package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.showInstruction("Press #e#b[Q]#k#n to view the Quest window.", 250, 5)
   return true
}