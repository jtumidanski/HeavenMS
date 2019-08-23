package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.playPortalSound()
   pi.warp(610020015, "CC6_A")
   return true
}