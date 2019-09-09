package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.haveItem(3992041, 1)) {
      pi.playPortalSound(); pi.warp(610030020, "out00")
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The giant gate of iron will not budge no matter what, however there is a visible key-shaped socket.")
      return false
   }
}