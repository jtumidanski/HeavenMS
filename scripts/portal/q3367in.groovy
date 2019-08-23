package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(3367)) {
      if (pi.getQuestProgress(3367, 31) < pi.getItemQuantity(4031797)) {
         pi.gainItem(4031797, (short) (pi.getQuestProgress(3367, 31) - pi.getItemQuantity(4031797)))
      }

      pi.playPortalSound(); pi.warp(926130102, 0)
      return true
   } else {
      pi.message("You don't have permission to access this room.")
      return false
   }
}