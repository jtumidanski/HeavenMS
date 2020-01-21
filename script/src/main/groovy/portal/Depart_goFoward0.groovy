package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   int mapId = pi.getPlayer().getMap().getId()

   if (mapId == 103040410 && pi.isQuestCompleted(2287)) {
      pi.playPortalSound()
      pi.warp(103040420, "right00")
      return true
   } else if (mapId == 103040420 && pi.isQuestCompleted(2288)) {
      pi.playPortalSound()
      pi.warp(103040430, "right00")
      return true
   } else if (mapId == 103040410 && pi.isQuestStarted(2287)) {
      pi.playPortalSound()
      pi.warp(103040420, "right00")
      return true
   } else if (mapId == 103040420 && pi.isQuestStarted(2288)) {
      pi.playPortalSound()
      pi.warp(103040430, "right00")
      return true
   } else {
      if (mapId == 103040440 || mapId == 103040450) {
         pi.playPortalSound()
         pi.warp(mapId + 10, "right00")
         return true
      }
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("CANNOT_ACCESS"))
      return false
   }
}