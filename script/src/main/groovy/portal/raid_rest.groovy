package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   int evLevel = ((pi.getMapId() - 1) % 5) + 1

   if (pi.getPlayer().getEventInstance().isEventLeader(pi.getPlayer()) && pi.getPlayer().getEventInstance().getPlayerCount() > 1) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("PARTY_LEADER_CANNOT_LEAVE"))
      return false
   }

   if (pi.getPlayer().getEventInstance().giveEventReward(pi.getPlayer(), evLevel)) {
      pi.playPortalSound()
      pi.warp(970030000)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("MAKE_ROOM_AVAILABLE_FOR_PRIZES"))
      return false
   }
}