package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2041025 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendYesNo(I18nMessage.from("2041025_BEEP_BEEP"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode > 0) {
         cm.warp(220080000)
      }
      cm.dispose()
   }
}

NPC2041025 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2041025(cm: cm))
   }
   return (NPC2041025) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }