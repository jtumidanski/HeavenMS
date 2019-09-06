package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.getMap().countPlayers() == 1) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.LIGHT_BLUE, "As the last player on this map, you are compelled to wait for the incoming keys.")
      return false
   } else {
      if (pi.haveItem(4001087)) {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.LIGHT_BLUE, "You cannot pass to the next map holding the 1st Crystal Key in your inventory.")
         return false
      }
      pi.playPortalSound(); pi.warp(240050101, 0)
      return true
   }
}