package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getJob().getJobNiche() == 2) {
      pi.playPortalSound(); pi.warp(610030521, 0)
      return true
   } else {
      pi.playerMessage(5, "Only mages may enter this portal.")
      return false
   }
}