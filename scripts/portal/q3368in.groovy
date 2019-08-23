package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if(pi.isQuestStarted(3368)) {
      pi.playPortalSound()
      pi.warp(926130103, 0)
      return true
   } else {
      pi.message("You don't have permission to access this room.")
      return false
   }
}