package portal


import scripting.portal.PortalPlayerInteraction
import server.maps.MapleMap

static def enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(6153) && pi.haveItem(4031475)) {
      MapleMap mapobj = pi.getWarpMap(910500200)
      if (mapobj.countPlayers() == 0) {
         pi.resetMapObjects(910500200)
         mapobj.shuffleReactors()
         pi.playPortalSound(); pi.warp(910500200, "out01")

         return true
      } else {
         pi.getPlayer().message("Some other player is currently inside.")
         return false
      }
   } else {
      pi.getPlayer().message("A mysterious force won't let you in.")
      return false
   }
}