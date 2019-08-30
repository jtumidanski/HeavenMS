package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getJob().getJobNiche() == 1) {
      pi.playPortalSound(); pi.warp(610030510, 0)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Only warriors may enter this portal.")
      return false
   }
}