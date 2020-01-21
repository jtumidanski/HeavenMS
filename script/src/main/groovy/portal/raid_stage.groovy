package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.getMap().getMonsters().isEmpty()) {
      int nextStage

      if (pi.getMapId() % 500 >= 100) {
         nextStage = pi.getMapId() + 100
      } else {
         nextStage = 970030001 + (Math.floor((pi.getMapId() - 970030100) / 500)).intValue()
      }

      pi.playPortalSound(); pi.warp(nextStage)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("DEFEAT_ALL_MONSTERS"))
      return false
   }
}