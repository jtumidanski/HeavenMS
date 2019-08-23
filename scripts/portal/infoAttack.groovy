package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(1035)) {
      pi.showInfo("UI/tutorial.img/20")
   }

   pi.blockPortal()
   return true
}