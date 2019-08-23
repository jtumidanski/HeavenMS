package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(1031)) {
      pi.showInfo("UI/tutorial.img/25")
   }

   pi.blockPortal()
   return true
}