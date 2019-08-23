package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.openNpc(1052125) //It is actually suppose to open the npc, because it leads to a boss map
   return true
}