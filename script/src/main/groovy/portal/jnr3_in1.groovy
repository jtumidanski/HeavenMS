package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.getMap().getReactorByName("jnr3_out2").getState() == (byte) 1) {
      pi.playPortalSound()
      pi.warp(926110202, 0)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The door is not opened yet.")
      return false
   }
}