package npc

import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9270038 {
   NPCConversationManager cm
   int status = -1
   int oldSelection = -1

   def start() {
      cm.sendSimple("Hello, I am Shalon from Singapore Airport. I can assist you in getting you to Kerning City in no time. Do you want to go to Kerning City?\r\n#b#L0#I would like to buy a plane ticket to Kerning City\r\n#b#L1#Let me go in to the departure point.")
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode <= 0) {
         oldSelection = -1
         cm.dispose()
      }

      if (status == 0) {
         if (selection == 0) {
            cm.sendYesNo("The ticket will cost you 5,000 mesos. Will you purchase the ticket?")
         } else if (selection == 1) {
            cm.sendYesNo("Would you like to go in now? You will lose your ticket once you go in! Thank you for choosing Wizet Airline.")
         }
         oldSelection = selection
      } else if (status == 1) {
         if (oldSelection == 0) {
            if (cm.getPlayer().getMeso() > 4999 && !cm.getPlayer().haveItem(4031732)) {
               if (cm.getPlayer().canHold(4031732, 1)) {
                  cm.gainMeso(-5000)
                  cm.gainItem(4031732)
                  cm.sendOk("Thank you for choosing Wizet Airline! Enjoy your flight!")
                  cm.dispose()
               } else {
                  cm.sendOk("You don't have a free slot on your ETC inventory for the ticket, please make a room beforehand.")
                  cm.dispose()
               }
            } else {
               cm.sendOk("You do not have enough mesos or you've already purchased a ticket.")
               cm.dispose()
            }
         } else if (oldSelection == 1) {
            if (cm.itemQuantity(4031732) > 0) {
               EventManager em = cm.getEventManager("AirPlane")
               if (em.getProperty("entry") == "true") {
                  cm.warp(540010001)
                  cm.gainItem(4031732, (short) -1)
               } else {
                  cm.sendOk("Sorry the plane has taken off, please wait a few minutes.")
               }
            } else {
               cm.sendOk("You need a #b#t4031732##k to get on the plane!")
            }
         }
         cm.dispose()
      }
   }
}

NPC9270038 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9270038(cm: cm))
   }
   return (NPC9270038) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }