package npc

import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2012025 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(4031576)) {
         EventManager em = cm.getEventManager("Genie")
         if (em.getProperty("entry") == "true") {
            cm.sendYesNo("This will not be a short flight, so you need to take care of some things, I suggest you do that first before getting on board. Do you still wish to board the genie?")
         } else {
            cm.sendOk("This genie is getting ready for takeoff. I'm sorry, but you'll have to get on the next ride. The ride schedule is available through the guide at the ticketing booth.")
            cm.dispose()
         }
      } else {
         cm.sendOk("Make sure you got an Ariant ticket to travel in this genie. Check your inventory.")
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode <= 0) {
         cm.sendOk("Okay, talk to me if you change your mind!")
         cm.dispose()
         return
      }

      EventManager em = cm.getEventManager("Genie")
      if (em.getProperty("entry") == "true") {
         cm.warp(200000152)
         cm.gainItem(4031576, (short) -1)
      } else {
         cm.sendOk("This genie is getting ready for takeoff. I'm sorry, but you'll have to get on the next ride. The ride schedule is available through the guide at the ticketing booth.")
      }

      cm.dispose()
   }
}

NPC2012025 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2012025(cm: cm))
   }
   return (NPC2012025) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }