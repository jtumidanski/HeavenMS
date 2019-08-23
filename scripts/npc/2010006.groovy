package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2010006 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.getPlayer().getStorage().sendStorage(cm.getClient(), 2010006)
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC2010006 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2010006(cm: cm))
   }
   return (NPC2010006) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }