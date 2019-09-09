package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   int mapid = pi.getPlayer().getMap().getId()

   if (mapid == 103040410 && pi.isQuestCompleted(2287)) {
      pi.playPortalSound()
      pi.warp(103040420, "right01")
      return true
   } else if (mapid == 103040420 && pi.isQuestCompleted(2288)) {
      pi.playPortalSound()
      pi.warp(103040430, "right01")
      return true
   } else if (mapid == 103040410 && pi.isQuestStarted(2287)) {
      pi.playPortalSound()
      pi.warp(103040420, "right01")
      return true
   } else if (mapid == 103040420 && pi.isQuestStarted(2288)) {
      pi.playPortalSound()
      pi.warp(103040430, "right01")
      return true
   } else {
      if (mapid == 103040440 || mapid == 103040450) {
         pi.playPortalSound()
         pi.warp(mapid + 10, "right01")
         return true
      }
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You cannot access this area.")
      return false
   }
}