package portal

import net.server.world.MaplePartyCharacter
import scripting.event.EventManager
import scripting.portal.PortalPlayerInteraction
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   int stage = ((Math.floor(pi.getMapId() / 100)) % 10) - 1
   EventManager em = pi.getEventManager("TD_Battle" + stage)
   if (em == null) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("TD_ENCOUNTERED_ERROR").with(stage))
      return false
   }

   if (pi.getParty() == null) {
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
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("BOSS_PARTY_MINIMUM"))
         return false
      }

      pi.playPortalSound()
      return true
   }
}