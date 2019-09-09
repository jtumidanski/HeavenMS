package npc

import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2012013 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(4031074)) {
         EventManager em = cm.getEventManager("Trains")
         if (em.getProperty("entry") == "true") {
            cm.sendYesNo("Do you want to go to Ludibrium?")
         } else {
            cm.sendOk("The train to Ludibrium is already travelling, please be patient for the next one.")
            cm.dispose()
         }
      } else {
         cm.sendOk("Make sure you got a Ludibrium ticket to travel in this train. Check your inventory.")
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode <= 0) {
         cm.sendOk("Okay, talk to me if you change your mind!")
         cm.dispose()
         return
      }
      EventManager em = cm.getEventManager("Trains")
      if (em.getProperty("entry") == "true") {
         cm.warp(200000122)
         cm.gainItem(4031074, (short) -1)
         cm.dispose()
      } else {
         cm.sendOk("The train to Ludibrium is ready to take off, please be patient for the next one.")
         cm.dispose()
      }
   }
}

NPC2012013 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2012013(cm: cm))
   }
   return (NPC2012013) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }