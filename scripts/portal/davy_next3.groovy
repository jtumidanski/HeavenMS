package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.getMap().getMonsters().size() == 0 && passedGrindMode(pi.getMap(), pi.getEventInstance())) {
      pi.playPortalSound(); pi.warp(925100400, 0) //next
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The portal is not opened yet.")
      return false
   }
}

static def passedGrindMode(MapleMap map, EventInstanceManager eim) {
   if (eim.getIntProperty("grindMode") == 0) {
      return true
   }
   return eim.activatedAllReactorsOnMap(map, 2511000, 2517999)
}