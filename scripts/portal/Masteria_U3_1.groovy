package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.hasItem(3992040)) {
      pi.playPortalSound()
      pi.warp(610010201, "sB2_1")
      return false
   }
   return true
}