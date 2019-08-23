package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2101007 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendNext("NO! Abdullah, I said 17 bedrooms, and 23 bathrooms! CALL THE CONSTRUCTION COMPANY AND CHANGE IT!")
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC2101007 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2101007(cm: cm))
   }
   return (NPC2101007) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }