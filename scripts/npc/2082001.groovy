package npc

import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2082001 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(4031045)) {
         EventManager em = cm.getEventManager("Cabin")
         if (em.getProperty("entry") == "true") {
            cm.sendYesNo("Do you wish to board the flight?")
         } else {
            cm.sendOk("The flight has not arrived yet. Come back soon.")
            cm.dispose()
         }
      } else {
         cm.sendOk("Make sure you got an Orbis ticket to travel in this flight. Check your inventory.")
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode <= 0) {
         cm.sendOk("Okay, talk to me if you change your mind!")
         cm.dispose()
         return
      }
      EventManager em = cm.getEventManager("Cabin")
      if (em.getProperty("entry") == "true") {
         cm.warp(240000111)
         cm.gainItem(4031045, (short) -1)
      } else {
         cm.sendOk("The flight has not arrived yet. Come back soon.")
      }
      cm.dispose()
   }
}

NPC2082001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2082001(cm: cm))
   }
   return (NPC2082001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }