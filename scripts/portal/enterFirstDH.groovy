package portal


import scripting.portal.PortalPlayerInteraction
import server.maps.MapleMap


static def enter(PortalPlayerInteraction pi) {
   int map = 0

   if (pi.getQuestStatus(20701) == 1) {
      map = 913000000
   } else if (pi.getQuestStatus(20702) == 1) {
      map = 913000100
   } else if (pi.getQuestStatus(20703) == 1) {
      map = 913000200
   }
   if (map > 0) {
      if (pi.getPlayerCount(map) == 0) {
         MapleMap mapp = pi.getMap(map)
         mapp.resetPQ()

         pi.playPortalSound()
         pi.warp(map, 0)
         return true
      } else {
         pi.playerMessage(5, "Someone is already in this map.")
         return false
      }
   } else {
      pi.playerMessage(5, "Hall #1 can only be entered if you're engaged in Kiku's Acclimation Training.")
      return false
   }
}