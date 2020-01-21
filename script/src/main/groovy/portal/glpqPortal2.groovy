package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   EventInstanceManager eim = pi.getEventInstance()
   if (eim != null) {
      pi.playPortalSound(); pi.warp(610030300, 0)

      if (eim.getIntProperty("glpq3") < 5 || eim.getIntProperty("glpq3_p") < 5) {
         if (eim.getIntProperty("glpq3_p") == 5) {
            MessageBroadcaster.getInstance().sendMapServerNotice(pi.getPlayer().getMap(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("ALL_SIGILS_NOT_ACTIVE"))
         } else {
            eim.setIntProperty("glpq3_p", eim.getIntProperty("glpq3_p") + 1)

            if (eim.getIntProperty("glpq3") == 5 && eim.getIntProperty("glpq3_p") == 5) {
               MessageBroadcaster.getInstance().sendMapServerNotice(pi.getPlayer().getMap(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("ANTELLION_NEXT"))

               eim.showClearEffect(610030300, "3pt", 2)
               eim.giveEventPlayersStageReward(3)
            } else {
               MessageBroadcaster.getInstance().sendMapServerNotice(pi.getPlayer().getMap(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("ADVENTURER_PASSED").with(5 - eim.getIntProperty("glpq3_p")))
            }
         }
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("PORTAL_ALREADY_OPENED"))
      }

      return true
   }

   return false
}