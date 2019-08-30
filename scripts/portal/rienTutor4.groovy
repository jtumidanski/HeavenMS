package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if (!pi.isQuestCompleted(21013)) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You must complete the quest before proceeding to the next map..")
      return false
   }
   pi.playPortalSound()
   pi.warp(140090500, 1)
   return true
}