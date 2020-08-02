package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201103 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.getLevel() >= 100) {
         cm.sendOk(I18nMessage.from("9201103_EXPEDITIONS"))
      } else {
         cm.sendOk(I18nMessage.from("9201103_INSIDE_THE_KEEP"))
      }
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9201103 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201103(cm: cm))
   }
   return (NPC9201103) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }