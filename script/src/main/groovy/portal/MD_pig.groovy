package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   int baseId = 100020000
   int dungeonId = 100020100
   int dungeons = 30

   if (pi.getMapId() == baseId) {
      if (pi.getParty() != null) {
         if (pi.isLeader()) {
            for (int i = 0; i < dungeons; i++) {
               if (pi.startDungeonInstance(dungeonId + i)) {
                  pi.playPortalSound()
                  pi.warp(dungeonId + i, "out00")
                  return true
               }
            }
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("PARTY_LEADER_MUST_ENTER"))
            return false
         }
      } else {
         for (int i = 0; i < dungeons; i++) {
            if (pi.startDungeonInstance(dungeonId + i)) {
               pi.playPortalSound()
               pi.warp(dungeonId + i, "out00")
               return true
            }
         }
      }
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("ALL_MINI_DUNGEON_IN_USE"))
      return false
   } else {
      pi.playPortalSound()
      pi.warp(baseId, "MD00")
      return true
   }
}