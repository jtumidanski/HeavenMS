package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.getEventInstance().gridInsert(pi.getPlayer(), 2)
   pi.playPortalSound()
   pi.warp(990000700, "st00")
   return true
}