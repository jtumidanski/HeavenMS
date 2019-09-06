package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.isQuestStarted(100202)) {
      pi.playPortalSound(); pi.warp(106020400, 2)
      return true
   } else if (pi.hasItem(4000507)) {
      pi.gainItem(4000507, (short) -1)
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You have used a Poison Spore to pass through the barrier.")

      pi.playPortalSound(); pi.warp(106020400, 2)
      return true
   }

   MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The overgrown vines is blocking the way.")
   return false
}