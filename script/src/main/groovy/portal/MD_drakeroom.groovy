package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   int baseid = 105090311
   int dungeonid = 105090320
   int dungeons = 30

   if (pi.getMapId() == baseid) {
      if (pi.getParty() != null) {
         if (pi.isLeader()) {
            for (int i = 0; i < dungeons; i++) {
               if (pi.startDungeonInstance(dungeonid + i)) {
                  pi.playPortalSound()
                  pi.warpParty(dungeonid + i, "out00")
                  return true
               }
            }
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Only solo or party leaders are supposed to enter the Mini-Dungeon.")
            return false
         }
      } else {
         for (int i = 0; i < dungeons; i++) {
            if (pi.startDungeonInstance(dungeonid + i)) {
               pi.playPortalSound()
               pi.warp(dungeonid + i, "out00")
               return true
            }
         }
      }
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "All of the Mini-Dungeons are in use right now, please try again later.")
      return false
   } else {
      pi.playPortalSound()
      pi.warp(baseid, "MD00")
      return true
   }
}