package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.getMap().countMonster(2220100) > 0) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("DEFEAT_BLUE_MUSHROOMS"))
      return false
   } else {
      EventInstanceManager eim = pi.getEventInstance()
      eim.stopEventTimer()
      eim.dispose()

      pi.playPortalSound()
      pi.warp(101000000, 26)

      if (pi.isQuestCompleted(20718)) {
         pi.openNpc(1103003, "MaybeItsGrendel_end")
      }

      return true
   }
}