package portal

import net.server.world.MapleParty
import net.server.world.MaplePartyCharacter
import scripting.event.EventManager
import scripting.portal.PortalPlayerInteraction
import tools.MessageBroadcaster
import tools.ServerNoticeType

boolean enter(PortalPlayerInteraction pi) {
   if (pi.isQuestCompleted(2331)) {
      pi.openNpc(1300013)
      return false
   }

   if (pi.isQuestCompleted(2333) && pi.isQuestStarted(2331) && !pi.hasItem(4001318)) {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Lost the Royal Seal, eh? Worry not! Kevin's code here to save your hide.")
      if (pi.canHold(4001318)) {
         pi.gainItem(4001318, (short) 1)
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Hey, how do you plan to hold this Seal when your inventory is full?")
      }
   }

   if (pi.isQuestCompleted(2333)) {
      pi.playPortalSound()
      pi.warp(106021600, 1)
      return true
   } else if (pi.isQuestStarted(2332) && pi.hasItem(4032388)) {
      pi.forceCompleteQuest(2332, 1300002)
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "You've found the princess!")
      pi.giveCharacterExp(4400, pi.getPlayer())

      EventManager em = pi.getEventManager("MK_PrimeMinister")
      Optional<MapleParty> party = pi.getPlayer().getParty()
      if (party.isPresent()) {
         MaplePartyCharacter[] eli = em.getEligibleParty(pi.getParty().orElseThrow())
         // thanks Conrad for pointing out missing eligible party declaration here
         if (eli.size() > 0) {
            if (em.startInstance(party.get(), pi.getMap(), 1)) {
               pi.playPortalSound()
               return true
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Another party is already challenging the boss in this channel.")
               return false
            }
         }
      } else {
         if (em.startInstance(pi.getPlayer())) { // thanks RedHat for noticing an issue here
            pi.playPortalSound()
            return true
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Another party is already challenging the boss in this channel.")
            return false
         }
      }
   } else if (pi.isQuestStarted(2333) || (pi.isQuestCompleted(2332) && !pi.isQuestStarted(2333))) {
      EventManager em = pi.getEventManager("MK_PrimeMinister")

      Optional<MapleParty> party = pi.getPlayer().getParty()
      if (party.isPresent()) {
         MaplePartyCharacter[] eli = em.getEligibleParty(pi.getParty().orElseThrow())
         if (eli.size() > 0) {
            if (em.startInstance(party.get(), pi.getMap(), 1)) {
               pi.playPortalSound()
               return true
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Another party is already challenging the boss in this channel.")
               return false
            }
         }
      } else {
         if (em.startInstance(pi.getPlayer())) {
            pi.playPortalSound()
            return true
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "Another party is already challenging the boss in this channel.")
            return false
         }
      }
   } else {
      MessageBroadcaster.getInstance().sendServerNotice(pi.getPlayer(), ServerNoticeType.PINK_TEXT, "The door seems to be locked. Perhaps I can find a key to open it...")
      return false
   }
}