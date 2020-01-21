package portal


import net.server.world.MaplePartyCharacter
import scripting.event.EventManager
import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (!pi.haveItem(4000381)) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("CAPTAIN_LATANICA_MISSING_ESSENCE"))
      return false
   } else {
      EventManager em = pi.getEventManager("LatanicaBattle")

      if (pi.getParty().isEmpty()) {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("BOSS_PARTY_NEEDED"))
         return false
      } else if (!pi.isLeader()) {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("BOSS_PARTY_LEADER_START"))
         return false
      } else {
         MaplePartyCharacter[] eli = em.getEligibleParty(pi.getParty().orElseThrow())
         if (eli.size() > 0) {
            if (!em.startInstance(pi.getParty().orElseThrow(), pi.getPlayer().getMap(), 1)) {
               MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("BOSS_ALREADY_STARTED"))
               return false
            }
         } else {  //this should never appear
            MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("BOSS_CANNOT_START_YET"))
            return false
         }

         pi.playPortalSound()
         return true
      }
   }
}