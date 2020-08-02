package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9101001 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      cm.sendNext(I18nMessage.from("9101001_FINISHED_ALL_TRAINING"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            cm.sendNextPrev(I18nMessage.from("9101001_BUT_REMEMBER"))
         } else if (status == 2) {
            cm.warp(40000, 0)
            cm.gainExp(3)
            cm.dispose()
         }
      }
   }
}

NPC9101001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9101001(cm: cm))
   }
   return (NPC9101001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }