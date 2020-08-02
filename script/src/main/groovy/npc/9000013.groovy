package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9000013 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      cm.sendNext(I18nMessage.from("9000013_IF_YOU_ARE_NOT_BUSY"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            cm.sendSimple(I18nMessage.from("9000013_WHAT_KIND_OF_EVENT"))
         } else if (status == 2) {
            if (selection == 0) {
               cm.sendNext(I18nMessage.from("9000013_3RD_ANNIVERSARY"))
               cm.dispose()
            } else if (selection == 1) {
               cm.sendSimple(I18nMessage.from("9000013_MANY_GAMES"))
            } else if (selection == 2) {
               cm.sendNext(I18nMessage.from("9000013_EVENT_HAS_NOT_BEEN_STARTED"))
               cm.dispose()
            }
         } else if (status == 3) {
            if (selection == 0) {
               cm.sendNext(I18nMessage.from("9000013_OLA_OLA_INFO"))
               cm.dispose()
            } else if (selection == 1) {
               cm.sendNext(I18nMessage.from("9000013_FITNESS_INFO"))
               cm.dispose()
            } else if (selection == 2) {
               cm.sendNext(I18nMessage.from("9000013_SNOWBALL_INFO"))
               cm.dispose()
            } else if (selection == 3) {
               cm.sendNext(I18nMessage.from("9000013_COCONUT_INFO"))
               cm.dispose()
            } else if (selection == 4) {
               cm.sendNext(I18nMessage.from("9000013_OX_QUIZ_INFO"))
               cm.dispose()
            } else if (selection == 5) {
               cm.sendNext(I18nMessage.from("9000013_TREASURE_INFO"))
               cm.dispose()
            }
         }
      }
   }
}

NPC9000013 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9000013(cm: cm))
   }
   return (NPC9000013) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }