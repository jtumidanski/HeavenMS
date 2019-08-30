package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if (pi.getEventInstance().getIntProperty("statusStg1") == 1) {
      pi.playPortalSound()
      pi.warp(926110001, 0) //next
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The portal is not opened yet.")
      return false
   }
}