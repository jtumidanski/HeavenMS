package portal

import scripting.event.EventManager
import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   EventManager nex = pi.getEventManager("GuardianNex")
   if(nex == null) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("GUARDIAN_NEX_ERROR"))
      return false
   }

   int[] quests = [3719, 3724, 3730, 3736, 3742, 3748]
   int[] mobs = [7120100, 7120101, 7120102, 8120100, 8120101, 8140510]

   for(int i = 0; i < quests.length; i++) {
      if (pi.isQuestActive(quests[i])) {
         if(pi.getQuestProgressInt(quests[i], mobs[i]) != 0) {
            MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("GUARDIAN_NEX_ALREADY_FACED"))
            return false
         }

         if(!nex.startInstance(i, pi.getPlayer())) {
            MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("GUARDIAN_NEX_SOMEONE_ALREADY_FACING"))
            return false
         } else {
            pi.playPortalSound()
            return true
         }
      }
   }

   MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("MYSTERIOUS_FORCE"))
   return false
}