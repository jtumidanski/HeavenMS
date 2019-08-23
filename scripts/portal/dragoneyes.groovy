package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.isQuestCompleted(22012)) {
      return false
   } else {
      pi.forceCompleteQuest(22012)
   }
   pi.blockPortal()
   return true
}