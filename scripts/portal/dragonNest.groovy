package portal

import scripting.event.EventManager
import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if (pi.isQuestCompleted(3706)) {
      pi.playPortalSound(); pi.warp(240040612, "out00")
      return true
   } else if (pi.isQuestStarted(100203) || pi.getPlayer().haveItem(4001094)) {
      EventManager em = pi.getEventManager("NineSpirit")
      if (!em.startInstance(pi.getPlayer())) {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "There is currently someone in this map, come back later.")
         return false
      } else {
         pi.playPortalSound()
         return true
      }
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "A strange force is blocking you from entering.")
      return false
   }
}