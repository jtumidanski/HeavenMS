package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if (pi.isQuestCompleted(3928) && pi.isQuestCompleted(3931) && pi.isQuestCompleted(3934)) {
      pi.playPortalSound(); pi.warp(260000201, 1)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Access restricted to only members of the Sand Bandits team.")
      return false
   }
}