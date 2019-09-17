package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (!pi.isQuestCompleted(21012)) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You must complete the quest before proceeding to the next map..")
      return false
   }
   pi.playPortalSound()
   pi.warp(140090400, 1)
   return true
}