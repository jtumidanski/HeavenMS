package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1061007 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendYesNo("Would you like to leave?")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 1) {
         cm.warp(105040300, 0)
      }
      cm.dispose()
   }
}

NPC1061007 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1061007(cm: cm))
   }
   return (NPC1061007) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }