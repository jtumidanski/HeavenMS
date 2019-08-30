package portal


import scripting.portal.PortalPlayerInteraction
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   MapleMap map = pi.getPlayer().getMap()
   if (pi.getPortal().getName() == "female00") {
      if (pi.getPlayer().getGender() == 1) {
         pi.playPortalSound(); pi.warp(map.getId(), "female01")
         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "This portal leads to the girls' area, try the portal at the other side.")
         return false
      }
   } else {
      if (pi.getPlayer().getGender() == 0) {
         pi.playPortalSound(); pi.warp(map.getId(), "male01")
         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "This portal leads to the boys' area, try the portal at the other side.")
         return false
      }
   }
}