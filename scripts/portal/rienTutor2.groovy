package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (!pi.isQuestCompleted(21011)) {
      pi.message("You must complete the quest before proceeding to the next map..")
      return false
   }
   pi.playPortalSound()
   pi.warp(140090300, 1)
   return true
}