package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if(pi.isQuestStarted(6134)) {
      if(pi.canHold(4031448)) {
         pi.gainItem(4031448, (short) 1)
         pi.playPortalSound()
         pi.warp(220070400, 3)

         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("MAKE_ROOM_FOR_QUEST_ITEM").with("ETC"))
         return false
      }
   } else {
      pi.playPortalSound()
      pi.warp(220070400, 3)
      return true
   }
}