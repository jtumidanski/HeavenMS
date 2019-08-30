package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if(pi.isQuestStarted(2073)) {
      pi.playPortalSound()
      pi.warp(900000000, 0)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Private property. This place can only be entered when running an errand from Camila.")
      return false
   }
}