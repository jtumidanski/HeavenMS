package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2141000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendAcceptDecline(I18nMessage.from("2141000_IF_ONLY_I_HAD"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 1) {
         cm.removeNpc(270050100, 2141000)
         cm.forceStartReactor(270050100, 2709000)
      }
      cm.dispose()
   }
}

NPC2141000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2141000(cm: cm))
   }
   return (NPC2141000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }