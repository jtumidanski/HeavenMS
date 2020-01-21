package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (!pi.canHold(4001261, 1)) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("INVENTORY_FULL_ERROR"))
      return false
   }
   pi.gainItem(4001261, (short) 1)
   pi.playPortalSound(); pi.warp(105100100, 0)
   return true
}