package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getMapId() == 240060000) {
      if (pi.getEventInstance().getIntProperty("defeatedHead") >= 1) {
         pi.playPortalSound(); pi.warp(240060100, 0)
         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.LIGHT_BLUE, "Horntail\'s Seal is Blocking this Door.")
         return false
      }
   } else if (pi.getPlayer().getMapId() == 240060100) {
      if (pi.getEventInstance().getIntProperty("defeatedHead") >= 2) {
         pi.playPortalSound(); pi.warp(240060200, 0)
         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.LIGHT_BLUE, "Horntail\'s Seal is Blocking this Door.")
         return false
      }
   }
   return false
}