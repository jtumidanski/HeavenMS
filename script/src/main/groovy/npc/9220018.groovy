package npc

import net.server.world.MaplePartyCharacter
import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9220018 {
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
            em = cm.getEventManager("TreasurePQ")
            if (em == null) {
               cm.sendOk("The Treasure PQ has encountered an error.")
               cm.dispose()
               return
            } else if (cm.isUsingOldPqNpcStyle()) {
               action((byte) 1, (byte) 0, 0)
               return
            }

            cm.sendSimple("#e#b<Party Quest: MV's Lair>\r\n#k#n" + em.getProperty("party") + "\r\n\r\nYou can't go any further because of the extremely dangerous creatures lying ahead. Would you like to collaborate with party members to complete the quest? If so, please have your #bparty leader#k talk to me.#b\r\n#L0#I want to participate in the party quest.\r\n#L1#I would like to " + (cm.getPlayer().isRecvPartySearchInviteEnabled() ? "disable" : "enable") + " Party Search.\r\n#L2#I would like to hear more details.")
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
               cm.sendOk("#e#b<Party Quest: MV's Lair>#k#n\r\nMV appeared once more, disrupting the welfare of the people of New Leaf City. Join forces with other maplers to fend off this sudden attack. After defeating MV and his minions, fetch your prizes at MV's treasure room.")
               cm.dispose()
            }
         }
      }
   }
}

NPC9220018 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9220018(cm: cm))
   }
   return (NPC9220018) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }