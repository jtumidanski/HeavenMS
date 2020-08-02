package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2091006 {
   NPCConversationManager cm
   int status = -2
   int sel = -1
   int readNotice = 0

   def start() {
      cm.sendSimple(I18nMessage.from("2091006_COURAGE_TO_CHALLENGE"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode == 0 && type == 0) {
         status -= 2
      }
      if (mode >= 0) {
         if (selection == 1 || readNotice == 1) {
            if (status == -1) {
               readNotice = 1
               cm.sendNext(I18nMessage.from("2091006_TAKE_THE_CHALLENGE"))
            } else if (status == 0) {
               cm.sendPrev(I18nMessage.from("2091006_CALL_YOUR_FRIENDS"))
            } else {
               cm.dispose()
            }
         } else {
            if (status == -1 && mode == 1) {
               cm.sendYesNo(I18nMessage.from("2091006_MYSTERIOUS_ENERGY"))
            } else if (status == 0) {
               if (mode == 0) {
                  cm.sendNext(I18nMessage.from("2091006_ENERGY_DISAPPEARED"))
               } else {
                  cm.getPlayer().saveLocation("MIRROR")
                  cm.warp(925020000, 4)
               }
               cm.dispose()
            }
         }
      } else {
         cm.dispose()
      }
   }
}

NPC2091006 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2091006(cm: cm))
   }
   return (NPC2091006) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }