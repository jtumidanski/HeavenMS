package npc
import tools.I18nMessage

import scripting.event.EventManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9270041 {
   NPCConversationManager cm
   int status = -1
   int oldSelection = -1

   def start() {
      cm.sendSimple(I18nMessage.from("9270041_HELLO"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode <= 0) {
         oldSelection = -1
         cm.dispose()
      }

      if (status == 0) {
         if (selection == 0) {
            cm.sendYesNo(I18nMessage.from("9270041_TICKET_COST"))
         } else if (selection == 1) {
            cm.sendYesNo(I18nMessage.from("9270041_GO_IN_NOW"))
         }
         oldSelection = selection
      } else if (status == 1) {
         if (oldSelection == 0) {
            if (cm.getPlayer().getMeso() > 4999 && !cm.getPlayer().haveItem(4031731)) {
               if (cm.canHold(4031731, 1)) {
                  cm.gainMeso(-5000)
                  cm.gainItem(4031731)
                  cm.sendOk(I18nMessage.from("9270041_THANK_YOU"))
                  cm.dispose()
               } else {
                  cm.sendOk(I18nMessage.from("9270041_NEED_ETC_SPACE"))
                  cm.dispose()
               }
            } else {
               cm.sendOk(I18nMessage.from("9270041_NOT_ENOUGH_MESOS"))
               cm.dispose()
            }
         } else if (oldSelection == 1) {
            if (cm.itemQuantity(4031731) > 0) {
               EventManager em = cm.getEventManager("AirPlane")
               if (em.getProperty("entry") == "true") {
                  cm.warp(540010100)
                  cm.gainItem(4031731, (short) -1)
               } else {
                  cm.sendOk(I18nMessage.from("9270041_WAIT_A_FEW_MINUTES"))
               }
            } else {
               cm.sendOk(I18nMessage.from("9270041_NEED_A_TICKET"))
            }
         }
         cm.dispose()
      }
   }
}

NPC9270041 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9270041(cm: cm))
   }
   return (NPC9270041) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }