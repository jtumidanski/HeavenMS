package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   int mapid = pi.getPlayer().getSavedLocation("MIRROR")

   pi.playPortalSound()
   if(mapid == 260020500) pi.warp(mapid, 3)
   else pi.warp(mapid)
   return true
}