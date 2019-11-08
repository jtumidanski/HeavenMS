package portal

import net.server.world.MaplePartyCharacter
import scripting.event.EventManager
import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (!((pi.isQuestStarted(6361) && pi.haveItem(4031870, 1)) || (pi.isQuestCompleted(6361) && !pi.isQuestCompleted(6363)))) {
      EventManager em = pi.getEventManager("PapulatusBattle")

      if (pi.getParty() == null) {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You are currently not in a party, create one to attempt the boss.")
         return false
      } else if (!pi.isLeader()) {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Your party leader must enter the portal to start the battle.")
         return false
      } else {
         MaplePartyCharacter[] eli = em.getEligibleParty(pi.getParty().orElseThrow())
         if (eli.size() > 0) {
            if (!em.startInstance(pi.getParty().orElseThrow(), pi.getPlayer().getMap(), 1)) {
               MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The battle against the boss has already begun, so you may not enter this place yet.")
               return false
            }
         } else {  //this should never appear
            MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You cannot start this battle yet, because either your party is not in the range size, some of your party members are not eligible to attempt it or they are not in this map. If you're having trouble finding party members, try Party Search.")
            return false
         }

         pi.playPortalSound()
         return true
      }
   } else {
      pi.playPortalSound()
      pi.warp(922020300, 0)
      return true
   }
}