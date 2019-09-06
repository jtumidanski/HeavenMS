package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.isQuestCompleted(22015)) {
      pi.playPortalSound()
      pi.warp(100030300, 2)
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Please rescue the baby pig!")//not gms like
   }
   return true
}