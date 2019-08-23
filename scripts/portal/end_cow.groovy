package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(2180) && (pi.hasItem(4031847) || pi.hasItem(4031848) || pi.hasItem(4031849) || pi.hasItem(4031850))) {
      if (pi.hasItem(4031850)) {
         pi.playPortalSound(); pi.warp(120000103)
         return true
      } else {
         pi.getPlayer().dropMessage(5, "Your milk jug is not full...")
         return false
      }
   } else {
      pi.playPortalSound(); pi.warp(120000103)
      return true
   }
}