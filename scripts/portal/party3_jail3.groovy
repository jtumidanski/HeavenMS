package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if(pi.getEventInstance().getIntProperty("statusStg8") == 1) {
      pi.playPortalSound()
      pi.warp(920010930,0)
      return true
   }
   else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The storage is currently inaccessible, as the powers of the Pixies remains active within the tower.")
      return false
   }
}