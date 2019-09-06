package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getMap().getMonsterById(9300216) != null) {
      pi.getPlayer().enteredScript("dojang_Msg", pi.getPlayer().getMap().getId())
      pi.getPlayer().setFinishedDojoTutorial()
      pi.playPortalSound(); pi.warp(925020001, 0)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "So Gong: Haha! You're going to run away like a coward? I won't let you get away that easily!")
      return false
   }
}