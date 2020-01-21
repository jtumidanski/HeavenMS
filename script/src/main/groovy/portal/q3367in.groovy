package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(3367)) {
      int booksDone = pi.getQuestProgressInt(3367, 31)
      int booksInv = pi.getItemQuantity(4031797)

      if (booksInv < booksDone) {
         pi.gainItem(4031797, (short) (booksDone - booksInv))
      }

      pi.playPortalSound()
      pi.warp(926130102, 0)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("ROOM_NO_ACCESS"))
      return false
   }
}