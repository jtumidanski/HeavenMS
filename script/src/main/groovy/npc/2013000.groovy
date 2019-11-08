package npc

import net.server.world.MaplePartyCharacter
import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Wonky
	Map(s): 		Orbis - The Unknown Tower
	Description: 	
*/


class NPC2013000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   EventManager em

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

         if (cm.getMapId() == 200080101) {
            if (status == 0) {
               em = cm.getEventManager("OrbisPQ")
               if (em == null) {
                  cm.sendOk("The Orbis PQ has encountered an error.")
                  cm.dispose()
                  return
               } else if (cm.isUsingOldPqNpcStyle()) {
                  action((byte) 1, (byte) 0, 0)
                  return
               }

               cm.sendSimple("#e#b<Party Quest: Tower of Goddess>\r\n#k#n" + em.getProperty("party") + "\r\n\r\nWould you like to assemble or join a team to solve the puzzles of the #bTower of Goddess#k? Have your #bparty leader#k talk to me or make yourself a party.#b\r\n#L0#I want to participate in the party quest.\r\n#L1#I would like to " + (cm.getPlayer().isRecvPartySearchInviteEnabled() ? "disable" : "enable") + " Party Search.\r\n#L2#I would like to hear more details.\r\n#L3#I would like to reclaim a prize.")
            } else if (status == 1) {
               if (selection == 0) {
                  if (cm.getParty().isEmpty()) {
                     cm.sendOk("You can participate in the party quest only if you are in a party.")
                     cm.dispose()
                  } else if (!cm.isLeader()) {
                     cm.sendOk("Your party leader must talk to me to start this party quest.")
                     cm.dispose()
                  } else {
                     MaplePartyCharacter[] eli = em.getEligibleParty(cm.getParty().orElseThrow())
                     if (eli.size() > 0) {
                        if (!em.startInstance(cm.getParty().orElseThrow(), cm.getPlayer().getMap(), 1)) {
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
               } else if (selection == 2) {
                  cm.sendOk("#e#b<Party Quest: Tower of Goddess>#k#n\r\nOur goddess has been missing since some time ago, rumor has it She has been seen last time inside the Tower of Goddess. Furthermore, our sanctuary has been seized by the overwhelming forces of the pixies, those beings that are recently wandering at the outskirts of Orbis. Their leader, Papa Pixie, currently holds the throne and may know Her whereabouts, so we urge to find a composition of brave heroes to charge into and claim back our sanctuary and rescue Her. If your team is able to be a composite of every job niche available (Warrior, Magician, Bowman, Thief and Pirate), you guys will receive my blessings to aid you in battle. Will you aid us?\r\n")
                  cm.dispose()
               } else {
                  cm.sendSimple("So, what prize do you want to obtain?\r\n#b#L0#Give me Goddess Wristband.\r\n")
               }
            } else if (status == 2) {
               if (selection == 0) {
                  if (!cm.haveItem(1082232) && cm.haveItem(4001158, 10)) {
                     cm.gainItem(1082232, (short) 1)
                     cm.gainItem(4001158, (short) -10)
                     cm.dispose()
                  } else {
                     cm.sendOk("You either have Goddess Wristband already or you do not have 10 #t4001158#.")
                     cm.dispose()
                  }
               }
            }
         } else {
            if (status == 0) {
               cm.sendYesNo("Are you going to drop out from this rescue mission?")
            } else if (status == 1) {
               cm.warp(920011200)
               cm.dispose()
            }
         }
      }
   }
}

NPC2013000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2013000(cm: cm))
   }
   return (NPC2013000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }