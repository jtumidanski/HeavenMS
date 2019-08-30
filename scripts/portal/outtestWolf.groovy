package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if (pi.getMap().countMonsters() == 0) {
      if (pi.canHold(4001193, 1)) {
         pi.gainItem(4001193, (short) 1)
         pi.playPortalSound()
         pi.warp(140010210, 0)
         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Free a slot on your inventory before receiving the couse clear's token.")
         return false
      }
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Defeat all wolves before exiting the stage.")
      return false
   }
}