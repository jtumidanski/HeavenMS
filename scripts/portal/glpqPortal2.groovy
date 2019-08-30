package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   EventInstanceManager eim = pi.getEventInstance()
   if (eim != null) {
      pi.playPortalSound(); pi.warp(610030300, 0)

      if (eim.getIntProperty("glpq3") < 5 || eim.getIntProperty("glpq3_p") < 5) {
         if (eim.getIntProperty("glpq3_p") == 5) {
            MessageBroadcaster.getInstance().sendMapServerNotice(pi.getPlayer().getMap(), ServerNoticeType.LIGHT_BLUE, "Not all Sigils have been activated yet. Make sure they have all been activated to proceed to the next stage.")
         } else {
            eim.setIntProperty("glpq3_p", eim.getIntProperty("glpq3_p") + 1)

            if (eim.getIntProperty("glpq3") == 5 && eim.getIntProperty("glpq3_p") == 5) {
               MessageBroadcaster.getInstance().sendMapServerNotice(pi.getPlayer().getMap(), ServerNoticeType.LIGHT_BLUE, "The Antellion grants you access to the next portal! Proceed!")

               eim.showClearEffect(610030300, "3pt", 2)
               eim.giveEventPlayersStageReward(3)
            } else {
               MessageBroadcaster.getInstance().sendMapServerNotice(pi.getPlayer().getMap(), ServerNoticeType.LIGHT_BLUE, "An adventurer has passed through! " + (5 - eim.getIntProperty("glpq3_p")) + " to go.")
            }
         }
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.LIGHT_BLUE, "The portal at the bottom has already been opened! Proceed there!")
      }

      return true
   }

   return false
}