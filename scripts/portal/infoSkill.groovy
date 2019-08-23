package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.isQuestCompleted(1035)) {
      pi.showInfo("UI/tutorial.img/23")
   }

   pi.blockPortal()
   return true
}