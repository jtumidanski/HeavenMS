package portal


import scripting.portal.PortalPlayerInteraction

boolean enter(PortalPlayerInteraction pi) {
   int toMap = pi.getPlayer().getSavedLocation("FREE_MARKET")
   pi.playPortalSound()
   pi.warp(toMap, pi.getMarketPortalId(toMap))
   return true
}