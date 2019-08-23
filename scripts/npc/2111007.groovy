package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2111007 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendOk("Hm... Don't doubt it because I'm a back street broker. Dealing with me is trust... I keep up my credit.")
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC2111007 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2111007(cm: cm))
   }
   return (NPC2111007) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }