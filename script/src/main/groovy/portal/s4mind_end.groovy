package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (!pi.getEventInstance().isEventCleared()) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You have to clear this mission before entering this portal.")
      return false
   } else {
      if (pi.isQuestStarted(6410)) {
         pi.setQuestProgress(6410, 6411, "p2")
      }

      pi.playPortalSound()
      pi.warp(925010400)
      return true
   }
}