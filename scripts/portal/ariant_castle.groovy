package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().haveItem(4031582)) {
      pi.playPortalSound()
      pi.warp(260000301, 5)
      return true
   } else {
      pi.playerMessage(5, "You can enter only if you have a Entry Pass to the Palace.")
      return false
   }
}