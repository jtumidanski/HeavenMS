package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.isEventLeader()) {
      EventInstanceManager eim = pi.getPlayer().getEventInstance()
      int target
      byte theWay = pi.getMap().getReactorByName("light").getState()
      if (theWay == (byte) 1) {
         target = 240050300 //light
      } else if (theWay == (byte) 3) {
         target = 240050310 //dark
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("HORNTAIL_HIT_LIGHT_BULB"))
         return false
      }

      pi.playPortalSound()
      eim.warpEventTeam(target)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("HORNTAIL_PARTY_LEADER"))
      return false
   }
}