package portal


import scripting.portal.PortalPlayerInteraction
import server.maps.MapleReactor
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   String name = pi.getPortal().getName().substring(2, 4)
   MapleReactor gate = pi.getPlayer().getMap().getReactorByName("gate" + name)
   if (gate != null && gate.getState() == (byte) 4) {
      pi.playPortalSound(); pi.warp(670010600, "gt" + name + "PIB")
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The gate is not opened yet.")
      return false
   }
}