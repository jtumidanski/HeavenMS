package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   if (pi.canHold(4001193, 1)) {
      pi.gainItem(4001193, (short) 1)
      pi.playPortalSound(); pi.warp(211050000, 4)
      return true
   } else {
      pi.playerMessage(5, "Free a slot on your inventory before receiving the couse clear's token.")
      return false
   }
}