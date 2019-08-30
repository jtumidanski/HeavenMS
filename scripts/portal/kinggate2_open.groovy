package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getMap().getReactorByName("kinggate").getState() == (byte) 1) {
      pi.playPortalSound()
      pi.warp(990000900, 2)
      if (pi.getPlayer().getEventInstance().getProperty("boss") != null && pi.getPlayer().getEventInstance().getProperty("boss") == "true") {
         pi.changeMusic("Bgm10/Eregos")
      }
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "This crack appears to be blocked off by the door nearby.")
      return false
   }
}