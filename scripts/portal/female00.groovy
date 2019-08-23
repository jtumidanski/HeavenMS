package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   int gender = pi.getPlayer().getGender()
   if (gender == 1) {
      pi.playPortalSound(); pi.warp(670010200, 4)
      return true
   } else {
      pi.getPlayer().dropMessage(5, "You cannot proceed past here.")
      return false
   }
}