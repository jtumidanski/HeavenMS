package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.hasItem(3992040)) {
      pi.playPortalSound()
      pi.warp(610010001, "sU2_1")
      return false
   }
   return true
}