package portal


import scripting.portal.PortalPlayerInteraction
import server.maps.MapleMap
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   int mapId = 0

   if (pi.getQuestStatus(20701) == 1) {
      mapId = 913000000
   } else if (pi.getQuestStatus(20702) == 1) {
      mapId = 913000100
   } else if (pi.getQuestStatus(20703) == 1) {
      mapId = 913000200
   }
   if (mapId > 0) {
      if (pi.getPlayerCount(mapId) == 0) {
         MapleMap map = pi.getMap(mapId)
         map.resetPQ()

         pi.playPortalSound()
         pi.warp(mapId, 0)
         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Someone is already in this map.")
         return false
      }
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Hall #1 can only be entered if you're engaged in Kiku's Acclimation Training.")
      return false
   }
}