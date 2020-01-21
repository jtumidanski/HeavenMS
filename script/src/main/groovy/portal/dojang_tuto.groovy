package portal


import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.getPlayer().getMap().getMonsterById(9300216) != null) {
      pi.getPlayer().enteredScript("dojang_Msg", pi.getPlayer().getMap().getId())
      pi.getPlayer().setFinishedDojoTutorial()
      pi.getClient().getChannelServer().resetDojo(pi.getPlayer().getMap().getId())
      pi.getClient().getChannelServer().dismissDojoSchedule(pi.getPlayer().getMap().getId(), pi.getParty().get())
      pi.playPortalSound(); pi.warp(925020001, 0)
      return true
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("DOJO_DONT_RUN"))
      return false
   }
}