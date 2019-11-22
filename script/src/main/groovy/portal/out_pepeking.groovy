package portal

import scripting.event.EventInstanceManager
import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   EventInstanceManager eim = pi.getEventInstance()
   if (eim != null) {
      eim.stopEventTimer()
      eim.dispose()
   }

   //3 Yetis
   int questProgress = pi.getQuestProgressInt(2330, 3300005) + pi.getQuestProgressInt(2330, 3300006) + pi.getQuestProgressInt(2330, 3300007);
   if (questProgress == 3 && !pi.hasItem(4032388)) {
      if (pi.canHold(4032388)) {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You have acquired a key to the Wedding Hall. King Pepe must have dropped it.")
         pi.gainItem(4032388, (short) 1)

         pi.playPortalSound()
         pi.warp(106021400, 2)
         return true
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Please make room in your ETC inventory.")
         return false
      }
   } else {
      pi.playPortalSound()
      pi.warp(106021400, 2)
      return true
   }
}