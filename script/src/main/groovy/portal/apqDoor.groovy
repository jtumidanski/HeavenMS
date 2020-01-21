package portal


import scripting.portal.PortalPlayerInteraction
import server.maps.MapleReactor
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   String name = pi.getPortal().getName().substring(2, 4)
   MapleReactor gate = pi.getPlayer().getMap().getReactorByName("gate" + name)
   if (gate != null && gate.getState() == (byte) 4) {
      pi.playPortalSound(); pi.warp(670010600, "gt" + name + "PIB")
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("GATE_NOT_YET_OPEN"))
      return false
   }
}