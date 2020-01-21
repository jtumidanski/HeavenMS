package portal

import scripting.event.EventManager
import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   EventManager em = pi.getEventManager("KerningTrain")
   if (!em.startInstance(pi.getPlayer())) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("WAGON_FULL"))
      return false
   }

   pi.playPortalSound()
   return true
}