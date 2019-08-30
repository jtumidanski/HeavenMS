package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getParty() != null && pi.isEventLeader() && pi.haveItem(4001055, 1)) {
      pi.playPortalSound()
      pi.getEventInstance().warpEventTeam(920010100)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Please get the leader in this portal, make sure you have the Root of Life.")
      return false
   }
}