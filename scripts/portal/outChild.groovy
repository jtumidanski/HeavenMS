package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (!pi.isQuestStarted(21001)) {
      pi.playPortalSound()
      pi.warp(914000220, 2)
      return true
   } else {
      pi.playPortalSound()
      pi.warp(914000400, 2)
      return true
   }
}