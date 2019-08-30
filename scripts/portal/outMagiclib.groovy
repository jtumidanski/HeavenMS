package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if (pi.getMap().countMonster(2220100) > 0) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Cannot leave until all Blue Mushrooms have been defeated.")
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