package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   pi.playPortalSound()
   pi.warp(200090010, 5)
   if (pi.getPlayer().getClient().getChannelServer().getEventSM().getEventManager("Boats").getProperty("haveBalrog") == "true") {
      pi.changeMusic("Bgm04/ArabPirate")
   }
   return true
}