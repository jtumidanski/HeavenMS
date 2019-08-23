package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2132001 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendNext("Call me Dark Lord. I will give thieves a place in society... watch in a few years!")
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC2132001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2132001(cm: cm))
   }
   return (NPC2132001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }