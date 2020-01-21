package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (!pi.isQuestCompleted(21011)) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("COMPLETE_QUEST_BEFORE_PROCEEDING"))
      return false
   }
   pi.playPortalSound()
   pi.warp(140090300, 1)
   return true
}