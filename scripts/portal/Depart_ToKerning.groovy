package portal

import scripting.event.EventManager
import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   EventManager em = pi.getEventManager("KerningTrain")
   if (!em.startInstance(pi.getPlayer())) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The passenger wagon is already full. Try again a bit later.")
      return false
   }

   pi.playPortalSound()
   return true
}