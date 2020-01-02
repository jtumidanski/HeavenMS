package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   int mapId = pi.getPlayer().getSavedLocation("MIRROR")

   pi.playPortalSound()
   if(mapId == 260020500) pi.warp(mapId, 3)
   else pi.warp(mapId)
   return true
}