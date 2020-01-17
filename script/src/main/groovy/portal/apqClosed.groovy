package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType
import tools.I18nMessage

boolean enter(PortalPlayerInteraction pi) {
   MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("GATE_IS_NOT_YET_OPENED"))
   return false
}