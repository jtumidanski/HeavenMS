package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Puro
	Map(s): 		Whale Between Lith harbor and Rien
	Description: 	
*/


class NPC1200006 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendOk("The current is serene, which means we may arrive in lith harbor earlier than expected.")
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC1200006 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1200006(cm: cm))
   }
   return (NPC1200006) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }