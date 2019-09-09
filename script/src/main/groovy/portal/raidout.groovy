package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   int map = pi.getPlayer().getSavedLocation("BOSSPQ")
   if (map == -1) {
      map = 100000000
   }

   pi.playPortalSound()
   pi.warp(map, 0)
   return true
}