package npc

import scripting.event.EventManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage
import tools.SimpleMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1052007 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int ticketSelection = -1
   boolean hasTicket = false
   boolean NLC = false
   EventManager em

   def start() {
      cm.sendSimple(I18nMessage.from("1052007_PICK_YOUR_DESTINATION"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      em = cm.getEventManager("Subway")

      if (mode == -1) {
         cm.dispose()
         return
      } else if (mode == 0) {
         cm.dispose()
         return
      } else {
         status++
      }
      if (status == 1) {
         if (selection == 0) {
            EventManager em = cm.getEventManager("KerningTrain")
            if (!em.startInstance(cm.getPlayer())) {
               cm.sendOk(I18nMessage.from("1052007_ALREADY_FULL"))
            }
            cm.dispose()
         } else if (selection == 1) {
            if (cm.haveItem(4031036) || cm.haveItem(4031037) || cm.haveItem(4031038)) {
               String text = I18nMessage.from("1052007_WHICH_TICKET").to(cm.getClient()).evaluate()
               for (def i = 0; i < 3; i++) {
                  if (cm.haveItem(4031036 + i)) {
                     text += "\r\n#b#L" + (i + 1) + "##t" + (4031036 + i) + "#"
                  }
               }
               cm.sendSimple(SimpleMessage.from(text))
               hasTicket = true
            } else {
               cm.sendOk(I18nMessage.from("1052007_NEED_A_TICKET"))
               cm.dispose()
            }
         } else if (selection == 2) {
            if (!cm.haveItem(4031711) && cm.getPlayer().getMapId() == 103000100) {
               cm.sendOk(I18nMessage.from("1052007_NEED_A_TICKET_BUY_ONE_FROM_BELL"))
               cm.dispose()
               return
            }
            if (em.getProperty("entry") == "true") {
               cm.sendYesNo(I18nMessage.from("1052007_DO_YOU_WANT_TO_GET_ON"))
            } else {
               cm.sendNext(I18nMessage.from("1052007_PLEASE_BE_PATIENT"))
               cm.dispose()
            }
         }
      } else if (status == 2) {
         if (hasTicket) {
            ticketSelection = selection
            if (ticketSelection > -1) {
               cm.gainItem(4031035 + ticketSelection, (short) -1)
               cm.warp(103000897 + (ticketSelection * 3), "st00")
               hasTicket = false
               cm.dispose()
               return
            }
         }

         if (cm.haveItem(4031711)) {
            if (em.getProperty("entry") == "false") {
               cm.sendNext(I18nMessage.from("1052007_PLEASE_BE_PATIENT"))
            } else {
               cm.gainItem(4031711, (short) -1)
               cm.warp(600010004)
            }

            cm.dispose()
         }
      }
   }
}

NPC1052007 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1052007(cm: cm))
   }
   return (NPC1052007) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }