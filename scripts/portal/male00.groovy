package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   int gender = pi.getPlayer().getGender()
   if (gender == 0) {
      pi.playPortalSound()
      pi.warp(670010200, 3)
      return true
   } else {
      pi.getPlayer().dropMessage(5, "You cannot proceed past here.")
      return false
   }
}