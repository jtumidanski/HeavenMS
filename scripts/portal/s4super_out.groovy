package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   int exit = pi.getEventInstance().getIntProperty("canLeave")
   if (exit == 0) {
      pi.message("You have to wait one minute before you can leave this place.")
      return false
   } else if (exit == 2) {
      pi.playPortalSound()
      pi.warp(912010200)
      return true
   } else {
      pi.playPortalSound()
      pi.warp(120000101)
      return true
   }
}