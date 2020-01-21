package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(22010) || pi.getPlayer().getJob().getId() != 2001) {
      pi.playPortalSound()
      pi.warp(100030310, 0)
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("CANNOT_ENTER_LUSH_FOREST_WITHOUT"))
   }
   return true
}