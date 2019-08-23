package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1091004 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.getPlayer().getStorage().sendStorage(cm.getClient(), 1091004)
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC1091004 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1091004(cm: cm))
   }
   return (NPC1091004) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }