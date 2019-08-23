package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.useItem(2210016)
   pi.playPortalSound()
   pi.warp(200090510, 0)
   return true
}