package portal


import scripting.portal.PortalPlayerInteraction
import server.maps.MapleReactor
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if (!(pi.isQuestStarted(100200) || pi.isQuestCompleted(100200))) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You need approval from the masters to battle. You may not attempt the boss right now.")
      return false
   }

   if (!pi.isQuestCompleted(100201)) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You haven't completed all the trials yet. You may not attempt the boss right now.")
      return false
   }

   if (!pi.haveItem(4001017)) {    // thanks Conrad for pointing out missing checks for token item and unused reactor
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You do not have the Eye of Fire. You may not face the boss.")
      return false
   }

   MapleReactor react = pi.getMap().getReactorById(2118002)
   if (react != null && react.getState() > 0) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The entrance is currently blocked.")
      return false
   }

   pi.playPortalSound()
   pi.warp(211042400, "west00")
   return true
}