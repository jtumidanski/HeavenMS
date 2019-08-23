package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Aqua Balloon
	Map(s): 		Hidden-Street <Stage 6>
	Description: 	
*/


class NPC2040041 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendOk("Try to find the right combination of numbers to reach the top.")
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC2040041 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2040041(cm: cm))
   }
   return (NPC2040041) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }