package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getMap().countMonsters() == 0) {
      pi.playPortalSound()
      pi.warp(910500200, "out00")
      return true
   }
   MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You must defeat all the monsters first.")
   return true
}