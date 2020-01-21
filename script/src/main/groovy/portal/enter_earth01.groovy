package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (!pi.haveItem(4031890)) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("WARP_CARD_NEEDED"))
      return false
   }

   pi.playPortalSound(); pi.warp(120000101, "earth01")
   return true
}