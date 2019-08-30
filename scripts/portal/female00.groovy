package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   int gender = pi.getPlayer().getGender()
   if (gender == 1) {
      pi.playPortalSound(); pi.warp(670010200, 4)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You cannot proceed past here.")
      return false
   }
}