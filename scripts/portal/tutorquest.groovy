package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getMapId() == 130030001) {
      if (pi.isQuestStarted(20010)) {
         pi.playPortalSound()
         pi.warp(130030002, 0)
         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Please click on the NPC first to receive a quest.")
      }
   } else if (pi.getPlayer().getMapId() == 130030002) {
      if (pi.isQuestCompleted(20011)) {
         pi.playPortalSound()
         pi.warp(130030003, 0)
         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Please complete the required quest before proceeding.")
      }
   } else if (pi.getPlayer().getMapId() == 130030003) {
      if (pi.isQuestCompleted(20012)) {
         pi.playPortalSound()
         pi.warp(130030004, 0)
         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Please complete the required quest before proceeding.")
      }
   } else if (pi.getPlayer().getMapId() == 130030004) {
      if (pi.isQuestCompleted(20013)) {
         pi.playPortalSound()
         pi.warp(130030005, 0)
         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Please complete the required quest before proceeding.")
      }
   }

   return false
}