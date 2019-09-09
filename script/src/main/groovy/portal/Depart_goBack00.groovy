package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   pi.playPortalSound()
   pi.warp(pi.getPlayer().getMap().getId() - 10, "left00")
   return true
}