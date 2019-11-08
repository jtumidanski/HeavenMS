package npc

import net.server.world.MaplePartyCharacter
import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Tory
	Map(s): 		
	Description: 	
*/


class NPC1012112 {
   NPCConversationManager cm
   int status = -1
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

         if (cm.getMapId() == 100000200) {
            if (status == 0) {
               em = cm.getEventManager("HenesysPQ")
               if (em == null) {
                  cm.sendOk("The Henesys PQ has encountered an error.")
                  cm.dispose()
                  return
               } else if (cm.isUsingOldPqNpcStyle()) {
                  action((byte) 1, (byte) 0, 0)
                  return
               }

               cm.sendSimple("#e#b<Party Quest: Primrose Hill>\r\n#k#n" + em.getProperty("party") + "\r\n\r\nI'm Tory. Inside here is a beautiful hill where the primrose blooms. There's a tiger that lives in the hill, Growlie, and he seems to be looking for something to eat. Would you like to head over to the hill of primrose and join forces with your party members to help Growlie out?#b\r\n#L0#I want to participate in the party quest.\r\n#L1#I would like to " + (cm.getPlayer().isRecvPartySearchInviteEnabled() ? "disable" : "enable") + " Party Search.\r\n#L2#I would like to hear more details.\r\n#L3#I would like to redeem an instance hat.")
            } else if (status == 1) {
               if (selection == 0) {
                  if (cm.getParty().isEmpty()) {
                     cm.sendOk("Hi there! I'm Tory. This place is covered with mysterious aura of the full moon, and no one person can enter here by him/herself.")
                     cm.dispose()
                  } else if (!cm.isLeader()) {
                     cm.sendOk("If you'd like to enter here, the leader of your party will have to talk to me. Talk to your party leader about this.")
                     cm.dispose()
                  } else {
                     MaplePartyCharacter[] eli = em.getEligibleParty(cm.getParty().orElseThrow())
                     if (eli.size() > 0) {
                        if (!em.startInstance(cm.getParty().orElseThrow(), cm.getPlayer().getMap(), 1)) {
                           cm.sendOk("Someone is already attempting the PQ. Please wait for them to finish, or find another channel.")
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
                  cm.sendOk("#e#b<Party Quest: Primrose Hill>#k#n\r\nCollect primrose seeds from the flowers at the bottom part of the map and drop them by the platforms above the stage. Primrose seed color must match to grow the seeds, so test until you find the correct combination. When all the seeds have been planted, that is, starting second part of the mission, scout the Moon Bunny while it prepares Rice Cakes for the hungry Growlie. Once Growlie becomes satisfied, your mission is complete.")
                  cm.dispose()
               } else {
                  cm.sendYesNo("So you want to exchange #b20 #b#t4001158##k for the instance-designed hat?")
               }
            } else {
               if (cm.hasItem(4001158, 20)) {
                  if (cm.canHold(1002798)) {
                     cm.gainItem(4001158, (short) -20)
                     cm.gainItem(1002798, (short) 20)
                     cm.sendNext("Here it is. Enjoy!")
                  }
               } else {
                  cm.sendNext("You don't have enough #t4001158# to buy it yet!")
               }

               cm.dispose()
            }
         } else if (cm.getMapId() == 910010100) {
            if (status == 0) {
               cm.sendYesNo("Thank you for aiding in the effort of feeding the Growlie. As a matter of fact, your team has already been rewarded for reaching this far. With this problem now solved, there is another issue happening right now, if you are interessed check #bTommy#k there for the info. So, are you returning straight to Henesys now?")
            } else if (status == 1) {
               if (cm.getEventInstance().giveEventReward(cm.getPlayer())) {
                  cm.warp(100000200)
               } else {
                  cm.sendOk("It seems you are short on space in one of your inventories. Please check that first to get rewarded properly.")
               }
               cm.dispose()
            }
         } else if (cm.getMapId() == 910010400) {
            if (status == 0) {
               cm.sendYesNo("So, are you returning to Henesys now?")
            } else if (status == 1) {
               if (cm.getEventInstance() == null) {
                  cm.warp(100000200)
               } else if (cm.getEventInstance().giveEventReward(cm.getPlayer())) {
                  cm.warp(100000200)
               } else {
                  cm.sendOk("It seems you are short on space in one of your inventories. Please check that first to get rewarded properly.")
               }
               cm.dispose()
            }
         }
      }
   }
}

NPC1012112 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1012112(cm: cm))
   }
   return (NPC1012112) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }