package npc

import scripting.event.EventManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2012001 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(4031047)) {
         EventManager em = cm.getEventManager("Boats")
         if (em.getProperty("entry") == "true") {
            cm.sendYesNo(I18nMessage.from("2012001_GO_TO_ELLINIA"))
         } else {
            cm.sendOk(I18nMessage.from("2012001_ALREADY_TRAVELLING"))
            cm.dispose()
         }
      } else {
         cm.sendOk(I18nMessage.from("2012001_NEED_A_TICKET"))
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode <= 0) {
         cm.sendOk(I18nMessage.from("2012001_OK"))
         cm.dispose()
         return
      }

      EventManager em = cm.getEventManager("Boats")
      if (em.getProperty("entry") == "true") {
         cm.warp(200000112)
         cm.gainItem(4031047, (short) -1)
         cm.dispose()
      } else {
         cm.sendOk(I18nMessage.from("2012001_BOAT_IS_READY_BE_PATIENT"))
         cm.dispose()
      }
   }
}

NPC2012001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2012001(cm: cm))
   }
   return (NPC2012001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }