package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if (pi.getEventInstance().getIntProperty("statusStg1") == 1) {
      pi.playPortalSound(); pi.warp(674030200, 0)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The tunnel is currently blocked.")
      return false
   }
}