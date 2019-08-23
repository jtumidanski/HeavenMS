package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if(pi.isQuestStarted(6134)) {
      pi.playPortalSound()
      pi.warp(922020000, 0)
      return true
   }

   pi.getPlayer().message("A mysterious force won't let you in.")
   return false
}