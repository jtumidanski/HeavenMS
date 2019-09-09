package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getJob().getId() == 2000) {
      if (pi.isQuestStarted(21015)) {
         pi.showInfoText("You must exit to the right in order to find Murupas.")
         return false
      } else if (pi.isQuestStarted(21016)) {
         pi.showInfoText("You must exit to the right in order to find Murupias.")
         return false
      } else if (pi.isQuestStarted(21017)) {
         pi.showInfoText("You must exit to the right in order to find MuruMurus.")
         return false
      }
   }
   pi.playPortalSound()
   pi.warp(140010000, 2)
   return true
}