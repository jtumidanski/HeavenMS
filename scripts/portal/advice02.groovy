package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.showInstruction("Press #e#b[Alt]#k#n to\r\\ JUMP.", 100, 5)
   return true
}