package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.isQuestCompleted(3928) && pi.isQuestCompleted(3931) && pi.isQuestCompleted(3934)) {
      pi.playPortalSound(); pi.warp(260000201, 1)
      return true
   } else {
      pi.message("Access restricted to only members of the Sand Bandits team.")
      return false
   }
}