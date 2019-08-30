package portal


import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   if (!pi.getEventInstance().isEventCleared()) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Your team has not yet completed the trials. Fetch the Fire Ore and give it to Aura first.")
      return false
   }

   if (pi.getEventInstance().gridCheck(pi.getPlayer()) == -1) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Your have yet to claim your prize. Talk to Aura.")
      return false
   }

   pi.playPortalSound()
   pi.warp(211042300)
   return true
}