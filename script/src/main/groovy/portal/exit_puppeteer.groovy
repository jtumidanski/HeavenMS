package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.getMap().countMonster(9300285) > 0) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Defeat the Puppeteer before leaving.")
      return false
   } else {
      EventInstanceManager eim = pi.getEventInstance()
      if (eim != null) {
         eim.stopEventTimer()
         eim.dispose()
      }

      pi.playPortalSound()
      pi.warp(105070300, 3)
      return true
   }
}