package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(21701)) {
      pi.playPortalSound()
      pi.warp(914010000, 1)
      return true
   } else if (pi.isQuestStarted(21702)) {
      pi.playPortalSound()
      pi.warp(914010100, 1)
      return true
   } else if (pi.isQuestStarted(21703)) {
      pi.playPortalSound()
      pi.warp(914010200, 1)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("PUO_LESSON_REQUIREMENT"))
      return false
   }
}