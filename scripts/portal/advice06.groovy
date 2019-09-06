package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   pi.showInstruction("Press the #e#b[Up]#k arrow#n to use the portal \r\\and move to the next map.", 230, 5)
   return true
}