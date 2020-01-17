package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction
import server.maps.MapleReactor
import tools.MessageBroadcaster
import tools.ServerNoticeType
import tools.I18nMessage

boolean enter(PortalPlayerInteraction pi) {
   MapleReactor react = pi.getMap().getReactorByName("mob0")

   if (react.getState() < 1) {
      react.forceHitReactor((byte) 1)

      EventInstanceManager eim = pi.getEventInstance()
      eim.setIntProperty("glpq1", 1)

      MessageBroadcaster.getInstance().sendServerNotice(pi.getEventInstance().getPlayers(), ServerNoticeType.PINK_TEXT, I18nMessage.from("STRANGE_FORCE"))
      pi.playPortalSound(); pi.warp(610030100, 0)

      pi.getEventInstance().showClearEffect()
      eim.giveEventPlayersStageReward(1)
      return true
   }

   MessageBroadcaster.getInstance().sendServerNotice(pi.getEventInstance().getPlayers(), ServerNoticeType.PINK_TEXT, I18nMessage.from("PORTAL_MALFUNCTION"))
   return false
}