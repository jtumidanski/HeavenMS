package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2060008 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.getPlayer().getStorage().sendStorage(cm.getClient(), 2060008)
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC2060008 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2060008(cm: cm))
   }
   return (NPC2060008) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }