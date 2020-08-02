package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9060000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   boolean completed

   def start() {
      completed = cm.haveItem(4031508, 5) && cm.haveItem(4031507, 5)

      if (completed) {
         cm.sendNext(I18nMessage.from("9060000_WOW"))
      } else {
         cm.sendYesNo(I18nMessage.from("9060000_HAVE_NOT_MET_REQUIREMENTS"))
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         cm.dispose()
         return
      }

      if (status == 0) {
         cm.sendOk(I18nMessage.from("9060000_SEND_YOU_BACK"))
      } else {
         if (completed) {
            cm.getEventInstance().clearPQ()
         } else {
            cm.warp(923010100, 0)
         }

         cm.dispose()
      }
   }
}

NPC9060000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9060000(cm: cm))
   }
   return (NPC9060000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }