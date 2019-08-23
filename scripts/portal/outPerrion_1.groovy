package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.message("You found a shortcut to the start of the underground temple.")
   pi.playPortalSound()
   pi.warp(105100000, 2)
   return true
}