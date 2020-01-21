package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   switch (pi.getMapId()) {
      case 930000000:
         pi.playPortalSound()
         pi.warp(930000100, 0)
         return true
         break
      case 930000100:
         if (pi.getMap().getMonsters().size() == 0) {
            pi.playPortalSound()
            pi.warp(930000200, 0)
            return true
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("ELIMINATE_MONSTERS"))
            return false
         }
         break
      case 930000200:
         if (pi.getMap().getReactorByName("spine") != null && pi.getMap().getReactorByName("spine").getState() < 4) {
            MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("SPINE_BLOCKS"))
            return false
         } else {
            pi.playPortalSound()
            pi.warp(930000300, 0) //assuming they cant get past reactor without it being gone
            return true
         }
         break

      default:
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("UNBOUND_PATH"))
         return false
   }
}