package npc

import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1032008 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(4031045)) {
         EventManager em = cm.getEventManager("Boats")
         if (em.getProperty("entry") == "true") {
            cm.sendYesNo("Do you want to go to Orbis?")
         } else {
            cm.sendOk("The boat to Orbis is already travelling, please be patient for the next one.")
            cm.dispose()
         }
      } else {
         cm.sendOk("Make sure you got a Orbis ticket to travel in this boat. Check your inventory.")
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode <= 0) {
         cm.sendOk("Okay, talk to me if you change your mind!")
         cm.dispose()
         return
      }
      EventManager em = cm.getEventManager("Boats")
      if (em.getProperty("entry") == "true") {
         cm.warp(101000301)
         cm.gainItem(4031045, (short) -1)
         cm.dispose()
      } else {
         cm.sendOk("The boat to Orbis is ready to take off, please be patient for the next one.")
         cm.dispose()
      }
   }
}

NPC1032008 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1032008(cm: cm))
   }
   return (NPC1032008) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }