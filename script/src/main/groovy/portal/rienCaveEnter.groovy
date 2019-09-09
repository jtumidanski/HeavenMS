package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(21201) || pi.isQuestStarted(21302)) { //aran first job
      pi.playPortalSound()
      pi.warp(140030000, 1)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Something seems to be blocking this portal!")
      return false
   }
}