package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (!pi.getEventInstance().isEventCleared()) {
      pi.message("You have to clear this mission before entering this portal.")
      return false
   } else {
      if (pi.isQuestStarted(6410)) {
         pi.setQuestProgress(6410, 0, 1)
      }

      pi.playPortalSound()
      pi.warp(925010400)
      return true
   }
}