package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1022005 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.getPlayer().getStorage().sendStorage(cm.getClient(), 1022005)
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC1022005 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1022005(cm: cm))
   }
   return (NPC1022005) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }