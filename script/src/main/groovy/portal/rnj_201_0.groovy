package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.getMap().getReactorByName("rnj31_out").getState() == (byte) 1) {
      pi.playPortalSound()
      pi.warp(926100200, 1)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("DOOR_NOT_YET_OPENED"))
      return false
   }
}