package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getMap().getReactorByName("ghostgate").getState() == (byte) 1) {
      pi.playPortalSound(); pi.warp(990000800, 0)
      return true
   } else {
      pi.playerMessage(5, "This way forward is not open yet.")
      return false
   }
}