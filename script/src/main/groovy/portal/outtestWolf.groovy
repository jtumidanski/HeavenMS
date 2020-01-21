package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.getMap().countMonsters() == 0) {
      if (pi.canHold(4001193, 1)) {
         pi.gainItem(4001193, (short) 1)
         pi.playPortalSound()
         pi.warp(140010210, 0)
         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("FREE_SPACE_FOR_COUSE_CLEAR_TOKEN"))
         return false
      }
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("DEFEAT_ALL_WOLVES"))
      return false
   }
}