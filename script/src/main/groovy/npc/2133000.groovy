package npc

import net.server.world.MaplePartyCharacter
import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2133000 {
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

         if (status == 0) {
            em = cm.getEventManager("EllinPQ")
            if (em == null) {
               cm.sendOk("The Ellin PQ has encountered an error.")
               cm.dispose()
               return
            } else if (cm.isUsingOldPqNpcStyle()) {
               action((byte) 1, (byte) 0, 0)
               return
            }

            cm.sendSimple("#e#b<Party Quest: Forest of Poison Haze>\r\n#k#n" + em.getProperty("party") + "\r\n\r\nWould you like to assemble or join a team to solve the puzzles of the #bForest of Poison Haze#k? Have your #bparty leader#k talk to me or make yourself a party.#b\r\n#L0#I want to participate in the party quest.\r\n#L1#I would like to " + (cm.getPlayer().isRecvPartySearchInviteEnabled() ? "disable" : "enable") + " Party Search.\r\n#L2#I would like to hear more details.\r\n#L3#I would like to reclaim a prize.")
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
            } else if (selection == 2) {
               cm.sendOk("#e#b<Party Quest: Forest of Poison Haze>#k#n\r\nIn this PQ, your mission is to progressively make your way through the woods, taking on all baddies in your path, solving many puzzles you encounter and rallying yourselves to take the best of teamwork to overcome time limits and powerful creatures. Clearing the final boss, your team have a chance to obtain a marble that, #bwhen dropped by the fountain at the exit map#k, will guarantee the team extra prizes. Good luck.")
               cm.dispose()
            } else {
               cm.sendSimple("So, what prize do you want to obtain?\r\n#b#L0#Give me Altaire Earrings.\r\n#L1#Give me Glittering Altaire Earrings.\r\n#L2#Give me Brilliant Altaire Earrings")
            }
         } else if (status == 2) {
            if (selection == 0) {
               if (!cm.haveItem(1032060) && cm.haveItem(4001198, 10)) {
                  cm.gainItem(1032060, (short) 1)
                  cm.gainItem(4001198, (short) -10)
                  cm.dispose()
               } else {
                  cm.sendOk("You either have Altair Earrings already or you do not have 10 Altair Fragments.")
                  cm.dispose()
               }
            } else if (selection == 1) {
               if (cm.haveItem(1032060) && !cm.haveItem(1032061) && cm.haveItem(4001198, 10)) {
                  cm.gainItem(1032060, (short) -1)
                  cm.gainItem(1032061, (short) 1)
                  cm.gainItem(4001198, (short) -10)
                  cm.dispose()
               } else {
                  cm.sendOk("You either don't have Altair Earrings already or you do not have 10 Altair Fragments.")
                  cm.dispose()
               }
            } else if (selection == 2) {
               if (cm.haveItem(1032061) && !cm.haveItem(1032101) && cm.haveItem(4001198, 10)) {
                  cm.gainItem(1032061, (short) -1)
                  cm.gainItem(1032101, (short) 1)
                  cm.gainItem(4001198, (short) -10)
                  cm.dispose()
               } else {
                  cm.sendOk("You either don't have Glittering Altair Earrings already or you do not have 10 Altair Fragments.")
                  cm.dispose()
               }
            }
         }
      }
   }
}

NPC2133000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2133000(cm: cm))
   }
   return (NPC2133000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }