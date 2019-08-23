package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Puro
	Map(s): 		Whale Between Lith harbor and Rien
	Description: 	
*/


class NPC1200005 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendOk("Ahhhh, this is so boring... The whale is controlling the ship so i'm left with nothing to do but look up and stare at the clouds.")
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC1200005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1200005(cm: cm))
   }
   return (NPC1200005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }