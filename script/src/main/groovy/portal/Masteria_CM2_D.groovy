package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   if (pi.hasItem(3992039)) {
      pi.playPortalSound()
      pi.warp(610020001, "CM2_E")
      return false
   }
   return true
}