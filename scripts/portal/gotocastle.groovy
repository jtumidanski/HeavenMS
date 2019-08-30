package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if (pi.isQuestCompleted(2324)) {
      pi.playPortalSound(); pi.warp(106020501, 0)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The path ahead is covered with sprawling vine thorns, only a Thorn Remover to clear this out...")
      return false
   }
}