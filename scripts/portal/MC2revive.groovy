package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getTeam() == (byte) 0) {
      pi.warp(pi.getMapId() - 100)
   } else {
      pi.warp(pi.getMapId() - 100)
   }
   return true
}