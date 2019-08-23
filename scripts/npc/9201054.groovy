package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201054 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendOk("The patrol in New Leaf City is always ready. No creatures are able to break through to the city.")
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9201054 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201054(cm: cm))
   }
   return (NPC9201054) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }