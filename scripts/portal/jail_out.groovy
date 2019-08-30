package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   long jailedTime = pi.getJailTimeLeft()

   if (jailedTime <= 0) {
      pi.playPortalSound(); pi.warp(300000010, "in01")
      return true
   } else {
      int seconds = Math.floor(jailedTime / 1000) % 60
      int minutes = (Math.floor(jailedTime / (1000 * 60)) % 60)
      int hours = (Math.floor(jailedTime / (1000 * 60 * 60)) % 24)

      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You have been caught in bad behaviour by the Maple POLICE. You've got to stay here for " + hours + " hours " + minutes + " minutes " + seconds + " seconds yet.")
      return false
   }
}