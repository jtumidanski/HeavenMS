package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9120200 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendYesNo(I18nMessage.from("9120200_WANT_TO_RETURN"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0) {
            cm.sendOk(I18nMessage.from("9120200_TALK_TO_ME"))
            cm.dispose()
         } else if (mode == 1) {
            status++
         }
         if (status == 1) {
            cm.warp(801000000)
            cm.dispose()
         }
      }
   }
}

NPC9120200 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9120200(cm: cm))
   }
   return (NPC9120200) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }