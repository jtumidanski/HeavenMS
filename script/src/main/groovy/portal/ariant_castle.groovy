package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().haveItem(4031582)) {
      pi.playPortalSound()
      pi.warp(260000301, 5)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You can enter only if you have a Entry Pass to the Palace.")
      return false
   }
}