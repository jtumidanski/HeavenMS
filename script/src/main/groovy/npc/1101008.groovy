package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1101008 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (status == 0 && mode == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            cm.sendSimple(I18nMessage.from("1101008_WAIT"))
         } else if (status == 1) {
            if (selection == 0) {
               cm.sendNext(I18nMessage.from("1101008_SERVE_UNDER_SHINSOO"))
            } else if (selection == 1) {
               cm.guideHint(1)
               cm.dispose()
            } else if (selection == 2) {
               cm.guideHint(2)
               cm.dispose()
            } else if (selection == 3) {
               cm.guideHint(3)
               cm.dispose()
            } else if (selection == 4) {
               cm.guideHint(4)
               cm.dispose()
            } else if (selection == 5) {
               cm.guideHint(5)
               cm.dispose()
            } else if (selection == 6) {
               cm.guideHint(6)
               cm.dispose()
            } else if (selection == 7) {
               cm.guideHint(7)
               cm.dispose()
            } else if (selection == 8) {
               cm.guideHint(8)
               cm.dispose()
            } else if (selection == 9) {
               cm.guideHint(9)
               cm.dispose()
            } else if (selection == 10) {
               cm.guideHint(10)
               cm.dispose()
            } else if (selection == 11) {
               cm.guideHint(11)
               cm.dispose()
            } else if (selection == 12) {
               cm.guideHint(12)
               cm.dispose()
            } else if (selection == 13) {
               cm.guideHint(13)
               cm.dispose()
            } else if (selection == 14) {
               cm.sendOk(I18nMessage.from("1101008_CYGNUS_KNIGHT_INFO"))
               cm.dispose()
            }
         } else if (status == 2) {
            cm.sendNextPrev(I18nMessage.from("1101008_ASK_ME_QUESTIONS_ANYTIME"))
            cm.dispose()
         }
      }
   }
}

NPC1101008 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1101008(cm: cm))
   }
   return (NPC1101008) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }