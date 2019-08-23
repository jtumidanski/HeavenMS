package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if(pi.isQuestStarted(6110)) {
      if(pi.getWarpMap(910500100).countPlayers() == 0) {
         pi.resetMapObjects(910500100)
         pi.playPortalSound()
         pi.warp(910500100, 0)

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