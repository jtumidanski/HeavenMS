package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1209003 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      cm.sendOk("We are departing to #bVictoria Island#k briefly. I've heard the #rBlack Magician#k himself cannot take that place on his grasp yet, thanks to #bthe seal that has been casted on that area#k. We pray for their safety, but if fortune does not favor the Heroes, at least we will be safe once we reach the continent.")
      cm.dispose()
   }
}

NPC1209003 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1209003(cm: cm))
   }
   return (NPC1209003) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }