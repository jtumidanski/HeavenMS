package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getParty() != null && pi.isEventLeader() && pi.haveItem(4001055, 1)) {
      pi.playPortalSound()
      pi.getEventInstance().warpEventTeam(920010100)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("NEED_TO_BE_LEADER_AND_HAVE_ROOT_OF_LIFE"))
      return false
   }
}