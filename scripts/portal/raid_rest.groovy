package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   int evLevel = ((pi.getMapId() - 1) % 5) + 1

   if (pi.getPlayer().getEventInstance().isEventLeader(pi.getPlayer()) && pi.getPlayer().getEventInstance().getPlayerCount() > 1) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Being the party leader, you cannot leave before your teammates leave first or you pass leadership.")
      return false
   }

   if (pi.getPlayer().getEventInstance().giveEventReward(pi.getPlayer(), evLevel)) {
      pi.playPortalSound()
      pi.warp(970030000)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Make a room available on all EQUIP, USE, SET-UP and ETC inventory to claim an instance prize.")
      return false
   }
}