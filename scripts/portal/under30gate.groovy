package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getLevel() <= 30) {
      pi.playPortalSound()
      pi.warp(990000640, 1)
      return true
   } else {
      pi.getPlayer().dropMessage(5, "You cannot proceed past this point.")
      return false
   }
}