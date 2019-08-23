package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.hasItem(3992039)) {
      pi.playPortalSound()
      pi.warp(610020001, "CM2_D")
      return false
   }
   return true
}