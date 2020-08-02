package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201105 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.getMapId() == 610020005) {
         cm.sendOk(I18nMessage.from("9201105_KEEP_LIES_AHEAD"))
      } else {
         cm.sendOk(I18nMessage.from("9201105_PROGRESS_IS_SPLENDID"))
      }
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9201105 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201105(cm: cm))
   }
   return (NPC9201105) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }