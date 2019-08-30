package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getMap().getReactorByName("metalgate").getState() == (byte) 1) {
      pi.playPortalSound()
      pi.warp(990000431, 0)
      return true
   }
   MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "This way forward is not open yet.")
   return false
}