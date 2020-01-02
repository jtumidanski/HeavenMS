package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class UnidentifiedNpc {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {

   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

UnidentifiedNpc getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new UnidentifiedNpc(cm: cm))
   }
   return (UnidentifiedNpc) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }