package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2132000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendOk("Hmmm! For you to make your way here, far away from the Camp, you must be one strong individual. Let's explore new areas and find a place to establish our own town!!")
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC2132000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2132000(cm: cm))
   }
   return (NPC2132000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }