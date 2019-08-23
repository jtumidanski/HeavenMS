package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.getMap().getMonsters().isEmpty()) {
      int nextStage

      if (pi.getMapId() % 500 >= 100) {
         nextStage = pi.getMapId() + 100
      } else {
         nextStage = 970030001 + (Math.floor((pi.getMapId() - 970030100) / 500)).intValue()
      }

      pi.playPortalSound(); pi.warp(nextStage)
      return true
   } else {
      pi.getPlayer().dropMessage(6, "Defeat all monsters before proceeding to the next stage.")
      return false
   }
}