package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1012116 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendNext("It looks like there's nothing suspecious in the area.")
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {
   }
}

NPC1012116 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1012116(cm: cm))
   }
   return (NPC1012116) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }