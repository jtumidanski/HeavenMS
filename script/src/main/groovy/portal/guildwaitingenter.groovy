package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   long entryTime = pi.getPlayer().getEventInstance().getProperty("entryTimestamp").toLong()
   long timeNow = System.currentTimeMillis()

   int timeLeft = Math.ceil((entryTime - timeNow) / 1000).toInteger()

   if(timeLeft <= 0) {
      pi.playPortalSound(); pi.warp(990000100, 0)
      return true
   }
   else { //cannot proceed while allies can still enter
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("PORTAL_OPEN_IN").with(timeLeft))
      return false
   }
}