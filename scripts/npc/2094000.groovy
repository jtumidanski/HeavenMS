package npc

import net.server.world.MaplePartyCharacter
import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2094000 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   EventManager em = null

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && status == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            em = cm.getEventManager("PiratePQ")
            if (em == null) {
               cm.sendOk("The Pirate PQ has encountered an error.")
               cm.dispose()
               return
            } else if (cm.isUsingOldPqNpcStyle()) {
               action((byte) 1, (byte) 0, 0)
               return
            }

            cm.sendSimple("#e#b<Party Quest: Pirate Ship>\r\n#k#n" + em.getProperty("party") + "\r\n\r\nHelp! My son has been kidnapped and is bound on the hands of the fearful #rLord Pirate#k. I need your help... Would you please assemble or join a team to save him? Have your #bparty leader#k talk to me or make yourself a party.#b\r\n#L0#I want to participate in the party quest.\r\n#L1#I would like to " + (cm.getPlayer().isRecvPartySearchInviteEnabled() ? "disable" : "enable") + " Party Search.\r\n#L2#I would like to hear more details.")
         } else if (status == 1) {
            if (selection == 0) {
               if (cm.getParty() == null) {
                  cm.sendOk("You can participate in the party quest only if you are in a party.")
                  cm.dispose()
               } else if (!cm.isLeader()) {
                  cm.sendOk("Your party leader must talk to me to start this party quest.")
                  cm.dispose()
               } else {
                  MaplePartyCharacter[] eli = em.getEligibleParty(cm.getParty())
                  if (eli.size() > 0) {
                     if (!em.startInstance(cm.getParty(), cm.getPlayer().getMap(), 1)) {
                        cm.sendOk("Another party has already entered the #rParty Quest#k in this channel. Please try another channel, or wait for the current party to finish.")
                     }
                  } else {
                     cm.sendOk("You cannot start this party quest yet, because either your party is not in the range size, some of your party members are not eligible to attempt it or they are not in this map. If you're having trouble finding party members, try Party Search.")
                  }

                  cm.dispose()
               }
            } else if (selection == 1) {
               boolean psState = cm.getPlayer().toggleRecvPartySearchInvite()
               cm.sendOk("Your Party Search status is now: #b" + (psState ? "enabled" : "disabled") + "#k. Talk to me whenever you want to change it back.")
               cm.dispose()
            } else {
               cm.sendOk("#e#b<Party Quest: Pirate Ship>#k#n\r\nIn this PQ, your mission is to progressively make your way through the ship, taking on all pirates and baddies in your path. Reaching the #rLord Pirate#k, depending on how many great chests you opened on the stages before, the boss will reveal himself even more powerful, so stay alert. Said chests, if opened, gives many extra rewards to your crew, it's worth a shot! Good luck.")
               cm.dispose()
            }
         }
      }
   }
}

NPC2094000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2094000(cm: cm))
   }
   return (NPC2094000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }