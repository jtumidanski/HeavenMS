package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2101003 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendNext (I18nMessage.from("2101003_DO_NOT_TRY_TO_START_TROUBLE"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      cm.dispose()
   }
}

NPC2101003 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2101003(cm: cm))
   }
   return (NPC2101003) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }