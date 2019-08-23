package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if(pi.isQuestStarted(6134)) {
      if(pi.canHold(4031448)) {
         pi.gainItem(4031448, (short) 1)
         pi.playPortalSound()
         pi.warp(220070400, 3)

         return true
      } else {
         pi.getPlayer().message("Make room on your ETC to receive the quest item.")
         return false
      }
   } else {
      pi.playPortalSound()
      pi.warp(220070400, 3)
      return true
   }
}