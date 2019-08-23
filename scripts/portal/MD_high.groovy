package portal


import scripting.portal.PortalPlayerInteraction

static def enter(PortalPlayerInteraction pi) {
   int baseid = 551030000
   int dungeonid = 551030001
   int dungeons = 19

   if (pi.getMapId() == baseid) {
      if (pi.getParty() != null) {
         if (pi.isLeader()) {
            for (int i = 0; i < dungeons; i++) {
               if (pi.startDungeonInstance(dungeonid + i)) {
                  pi.playPortalSound()
                  pi.warpParty(dungeonid + i)
                  return true
               }
            }
         } else {
            pi.playerMessage(5, "Only solo or party leaders are supposed to enter the Mini-Dungeon.")
            return false
         }
      } else {
         for (int i = 0; i < dungeons; i++) {
            if (pi.startDungeonInstance(dungeonid + i)) {
               pi.playPortalSound()
               pi.warp(dungeonid + i)
               return true
            }
         }
      }
      pi.playerMessage(5, "All of the Mini-Dungeons are in use right now, please try again later.")
      return false
   } else {
      pi.playPortalSound()
      pi.warp(baseid, "MD00")
      return true
   }
}