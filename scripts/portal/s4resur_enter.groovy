package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if(pi.isQuestStarted(6134)) {
      pi.playPortalSound()
      pi.warp(922020000, 0)
      return true
   }

   MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "A mysterious force won't let you in.")
   return false
}