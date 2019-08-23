package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (!pi.isQuestStarted(3309) || pi.haveItem(4031708, 1)) {
      pi.playPortalSound()
      pi.warp(261020700, "down00")
   } else {
      pi.playPortalSound()
      pi.warp(926120000, "out00")
   }

   return true
}