package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9000002 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else if (mode == 0) {
         cm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            cm.sendNext(I18nMessage.from("9000002_BAM_BAM"))
         } else if (status == 1) {
            cm.sendNext(I18nMessage.from("9000002_WINNING_PRIZE"))
         } else if (status == 2) {
            cm.sendNext(I18nMessage.from("9000002_SOMETHING_GOOD_IS_BOUND_TO_HAPPEN"))
         } else if (status == 3) {
            if (cm.canHold(4031019)) {
               cm.gainItem(4031019)
               cm.warp(cm.getPlayer().getSavedLocation("EVENT"))
               cm.dispose()
            } else {
               cm.sendNext(I18nMessage.from("9000002_NEED_ETC_SPACE"))
            }
         } else if (status == 4) {
            cm.dispose()
         }
      }
   }
}

NPC9000002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9000002(cm: cm))
   }
   return (NPC9000002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }