package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.getMap().countMonster(9300285) > 0) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("MUST_DEFEAT_PUPPETEER"))
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