package portal

import scripting.event.EventManager
import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.isQuestCompleted(3706)) {
      pi.playPortalSound(); pi.warp(240040612, "out00")
      return true
   } else if (pi.isQuestStarted(100203) || pi.getPlayer().haveItem(4001094)) {
      EventManager em = pi.getEventManager("NineSpirit")
      if (!em.startInstance(pi.getPlayer())) {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("SOMEONE_IN_MAP"))
         return false
      } else {
         pi.playPortalSound()
         return true
      }
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("STRANGE_FORCE_2"))
      return false
   }
}