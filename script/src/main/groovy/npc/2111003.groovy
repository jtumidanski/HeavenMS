package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2111003 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.isQuestStarted(3335) && !cm.haveItem(4031695, 1)) {
         cm.warp(926120300, "out00")
         cm.dispose()
      } else {
         cm.sendOk(I18nMessage.from("2111003_IS_IT_REAL"))
         cm.dispose()
      }
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC2111003 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2111003(cm: cm))
   }
   return (NPC2111003) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }