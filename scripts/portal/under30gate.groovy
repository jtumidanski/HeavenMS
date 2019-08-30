package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getLevel() <= 30) {
      pi.playPortalSound()
      pi.warp(990000640, 1)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You cannot proceed past this point.")
      return false
   }
}