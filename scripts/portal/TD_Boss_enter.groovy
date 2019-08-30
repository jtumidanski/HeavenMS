package portal

import net.server.world.MaplePartyCharacter
import scripting.event.EventManager
import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

static def enter(PortalPlayerInteraction pi) {
   int stage = ((Math.floor(pi.getMapId() / 100)) % 10) - 1
   EventManager em = pi.getEventManager("TD_Battle" + stage)
   if (em == null) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "TD Battle " + stage + " encountered an unexpected error and is currently unavailable.")
      return false
   }

   if (pi.getParty() == null) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You are currently not in a party, create one to attempt the boss.")
      return false
   } else if (!pi.isLeader()) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Your party leader must enter the portal to start the battle.")
      return false
   } else {
      MaplePartyCharacter[] eli = em.getEligibleParty(pi.getParty())
      if (eli.size() > 0) {
         if (!em.startInstance(pi.getParty(), pi.getPlayer().getMap(), 1)) {
            MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The battle against the boss has already begun, so you may not enter this place yet.")
            return false
         }
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Your party must consist of at least 2 players to attempt the boss.")
         return false
      }

      pi.playPortalSound()
      return true
   }
}