package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2040025 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   int map = 221024400

   def start() {
      if (cm.haveItem(4001020)) {
         cm.sendSimple(I18nMessage.from("2040025_CHOICES"))
      } else {
         cm.sendOk(I18nMessage.from("2040025_NEED_SCROLL"))
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (status >= 0 && mode == 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 1) {
            if (selection == 0) {
               cm.sendYesNo(I18nMessage.from("2040025_TO_100"))
            } else {
               cm.sendYesNo(I18nMessage.from("2040025_TO_41"))
               map = 221021700
            }
         } else if (status == 2) {
            cm.gainItem(4001020, (short) -1)
            cm.warp(map, 3)
            cm.dispose()
         }
      }
   }
}

NPC2040025 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040025(cm: cm))
   }
   return (NPC2040025) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }