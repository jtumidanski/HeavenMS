package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   if (pi.isQuestCompleted(20730) || pi.isQuestCompleted(21734)) {  // puppeteer defeated, new found secret path
      pi.playPortalSound(); pi.warp(105040201, 2)
      return true
   }

   pi.openNpc(1063011, "PupeteerPassword")
   return false
}