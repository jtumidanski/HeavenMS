package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(6242)) {
      if (pi.getWarpMap(921100210).countPlayers() == 0) {
         pi.resetMapObjects(921100210)
         pi.playPortalSound()
         pi.warp(921100210, 0)

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