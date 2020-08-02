package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9000000 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      cm.sendNext(I18nMessage.from("9000000_NOT_BUSY"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            cm.sendSimple(I18nMessage.from("9000000_WHAT_KIND_OF_AN_EVENT"))
         } else if (status == 2) {
            if (selection == 0) {
               cm.sendNext(I18nMessage.from("9000000_ALL_THIS_MONTH"))
               cm.dispose()
            } else if (selection == 1) {
               cm.sendSimple(I18nMessage.from("9000000_MANY_GAMES"))
            } else if (selection == 2) {
               cm.sendNext(I18nMessage.from("9000000_NOT_BEEN_STARTED"))
               cm.dispose()
            }
         } else if (status == 3) {
            if (selection == 0) {
               cm.sendNext(I18nMessage.from("9000000_OLA_OLA_INFO"))
               cm.dispose()
            } else if (selection == 1) {
               cm.sendNext(I18nMessage.from("9000000_FITNESS_INFO"))
               cm.dispose()
            } else if (selection == 2) {
               cm.sendNext(I18nMessage.from("9000000_SNOWBALL_INFO"))
               cm.dispose()
            } else if (selection == 3) {
               cm.sendNext(I18nMessage.from("9000000_COCONUT_INFO"))
               cm.dispose()
            } else if (selection == 4) {
               cm.sendNext(I18nMessage.from("9000000_OX_QUIZ_INFO"))
               cm.dispose()
            } else if (selection == 5) {
               cm.sendNext(I18nMessage.from("9000000_TREASURE_HUNT_INFO"))
               cm.dispose()
            }
         }
      }
   }
}

NPC9000000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9000000(cm: cm))
   }
   return (NPC9000000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }