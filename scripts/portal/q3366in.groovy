package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(3366)) {
      pi.playPortalSound()
      pi.warp(926130101, 0)
      return true
   } else {
      pi.message("You don't have permission to access this room.")
      return false
   }
}