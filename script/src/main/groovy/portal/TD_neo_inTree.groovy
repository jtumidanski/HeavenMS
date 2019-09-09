package portal

import scripting.event.EventManager
import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   EventManager nex = pi.getEventManager("GuardianNex")
   if(nex == null) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Guardian Nex challenge encountered an error and is unavailable.")
      return false
   }

   int[] quests = [3719, 3724, 3730, 3736, 3742, 3748]
   int[] mobs = [7120100, 7120101, 7120102, 8120100, 8120101, 8140510]

   for(int i = 0; i < quests.length; i++) {
      if (pi.isQuestActive(quests[i])) {
         if(pi.getQuestProgress(quests[i], mobs[i]) != 0) {
            MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You already faced Nex. Complete your mission.")
            return false
         }

         if(!nex.startInstance(i, pi.getPlayer())) {
            MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Someone is already challenging Nex. Wait for them to finish before you enter.")
            return false
         } else {
            pi.playPortalSound()
            return true
         }
      }
   }

   MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "A mysterious force won't let you in.")
   return false
}