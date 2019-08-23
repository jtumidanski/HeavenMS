package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if(pi.isQuestStarted(3935) && !pi.haveItem(4031574, 1)) {
      if(pi.getWarpMap(926000010).countPlayers() == 0) {
         pi.playPortalSound()
         pi.warp(926000010)
         return true
      } else {
         pi.message("Someone is already trying this map.")
         return false
      }
   } else {
      return false
   }
}