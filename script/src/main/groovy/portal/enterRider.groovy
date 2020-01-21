package portal

import scripting.event.EventManager
import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(21610) && pi.haveItem(4001193, 1)) {
      EventManager em = pi.getEventManager("Aran_2ndmount")
      if (em == null) {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("2ND_MOUNT_QUEST_CLOSED"))
         return false
      } else {
         if (!em.startInstance(pi.getPlayer())) {
            MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("2ND_MOUNT_QUEST_SOMEONE_ALREADY_IN"))
            return false
         } else {
            pi.playPortalSound()
            return true
         }
      }
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("2ND_MOUNT_QUEST_REQUIREMENT"))
      return false
   }
}