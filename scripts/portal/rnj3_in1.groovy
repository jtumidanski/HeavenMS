package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if (pi.getMap().getReactorByName("rnj3_out2").getState() == (byte) 1) {
      pi.playPortalSound()
      pi.warp(926100202, 0)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The door is not opened yet.")
      return false
   }
}