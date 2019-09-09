package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(3367)) {
      if (pi.getQuestProgress(3367, 31) < pi.getItemQuantity(4031797)) {
         pi.gainItem(4031797, (short) (pi.getQuestProgress(3367, 31) - pi.getItemQuantity(4031797)))
      }

      pi.playPortalSound(); pi.warp(926130102, 0)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You don't have permission to access this room.")
      return false
   }
}