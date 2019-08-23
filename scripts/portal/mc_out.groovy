package portal


import scripting.portal.PortalPlayerInteraction
import server.maps.MapleMap

static def enter(PortalPlayerInteraction pi) {
   int returnMap = pi.getPlayer().getSavedLocation("MONSTER_CARNIVAL")
   if (returnMap < 0) {
      returnMap = 102000000 // Just Incase there is no saved location.
   }
   MapleMap target = pi.getPlayer().getClient().getChannelServer().getMapFactory().getMap(returnMap)
   pi.getPlayer().changeMap(target)
   pi.playPortalSound()
   return true
}