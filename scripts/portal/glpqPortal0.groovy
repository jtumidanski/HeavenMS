package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.getEventInstance().getIntProperty("glpq1") == 0) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getEventInstance().getPlayers(), ServerNoticeType.PINK_TEXT, "This path is currently blocked.")
      return false

   } else {
      pi.playPortalSound(); pi.warp(610030200, 0)
      return true
   }
}