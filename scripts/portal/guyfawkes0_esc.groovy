package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.getEventInstance().getIntProperty("statusStg1") == 1) {
      pi.playPortalSound(); pi.warp(674030200, 0)
      return true
   } else {
      pi.message("The tunnel is currently blocked.")
      return false
   }
}