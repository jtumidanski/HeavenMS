package portal


import scripting.portal.PortalPlayerInteraction
import server.maps.MapleReactor
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (!(pi.isQuestStarted(100200) || pi.isQuestCompleted(100200))) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("ZAKUM_MASTER_APPROVAL"))
      return false
   }

   if (!pi.isQuestCompleted(100201)) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("ZAKUM_COMPLETE_TRIALS_2"))
      return false
   }

   if (!pi.haveItem(4001017)) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("ZAKUM_NEED_EYE_OF_FIRE"))
      return false
   }

   MapleReactor react = pi.getMap().getReactorById(2118002)
   if (react != null && react.getState() > 0) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("ENTRANCE_BLOCKED"))
      return false
   }

   pi.playPortalSound()
   pi.warp(211042400, "west00")
   return true
}