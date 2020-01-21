package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(100202)) {
      pi.playPortalSound(); pi.warp(106020400, 2)
      return true
   } else if (pi.hasItem(4000507)) {
      pi.gainItem(4000507, (short) -1)
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("POISON_SPORE_USED"))

      pi.playPortalSound(); pi.warp(106020400, 2)
      return true
   }

   MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("OVERGROWN_VINES"))
   return false
}