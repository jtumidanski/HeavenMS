package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(21701)) {
      pi.playPortalSound()
      pi.warp(914010000, 1)
      return true
   } else if (pi.isQuestStarted(21702)) {
      pi.playPortalSound()
      pi.warp(914010100, 1)
      return true
   } else if (pi.isQuestStarted(21703)) {
      pi.playPortalSound()
      pi.warp(914010200, 1)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You will be allowed to enter the Penguin Training Ground only if you are receiving a lesson from Puo.")
      return false
   }
}