package portal

import net.server.Server
import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getMapId() != 777777777) {
      if (!Server.getInstance().canEnterDeveloperRoom()) {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The next room is currently unavailable.")
         return false
      }

      pi.getPlayer().saveLocation("DEVELOPER")
      pi.playPortalSound()
      pi.warp(777777777, "out00")
   } else {
      int toMap = pi.getPlayer().getSavedLocation("DEVELOPER")
      pi.playPortalSound()
      pi.warp(toMap, "in00")
   }

   return true
}