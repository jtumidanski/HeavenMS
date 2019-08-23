package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.showInstruction("You can check your character's stats by pressing the #e#b[S]#k#nkey.", 350, 5)
   return true
}