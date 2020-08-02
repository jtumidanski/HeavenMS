package npc

import scripting.event.EventManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2012021 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(4031331)) {
         EventManager em = cm.getEventManager("Cabin")
         if (em.getProperty("entry") == "true") {
            cm.sendYesNo(I18nMessage.from("2012021_BOARD_THE_FLIGHT"))
         } else {
            cm.sendOk(I18nMessage.from("2012021_FLIGHT_HAS_NOT_ARRIVED"))
            cm.dispose()
         }
      } else {
         cm.sendOk(I18nMessage.from("2012021_NEED_TICKET"))
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode <= 0) {
         cm.sendOk(I18nMessage.from("2012021_OKAY"))
         cm.dispose()
         return
      }

      EventManager em = cm.getEventManager("Cabin")
      if (em.getProperty("entry") == "true") {
         cm.warp(200000132)
         cm.gainItem(4031331, (short) -1)
      } else {
         cm.sendOk(I18nMessage.from("2012021_FLIGHT_HAS_NOT_ARRIVED"))
      }
      cm.dispose()
   }
}

NPC2012021 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2012021(cm: cm))
   }
   return (NPC2012021) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }