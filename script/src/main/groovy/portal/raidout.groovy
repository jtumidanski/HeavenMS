package portal


import scripting.portal.PortalPlayerInteraction
import server.maps.SavedLocationType

boolean enter(PortalPlayerInteraction pi) {
   int map = pi.getPlayer().getSavedLocation(SavedLocationType.BOSS_PQ.toString())
   if (map == -1) {
      map = 100000000
   }

   pi.playPortalSound()
   pi.warp(map, 0)
   return true
}