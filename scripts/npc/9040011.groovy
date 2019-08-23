package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9040011 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendOk("<Notice> \r\n Are you part of a Guild that possesses an ample amount of courage and trust? Then take on the Guild Quest and challenge yourselves!\r\n\r\n#bTo Participate :#k\r\n1. The Guild must consist of at least 6 people!\r\n2. The leader of the Guild Quest must be a Master or a Jr. Master of the Guild!\r\n3. The Guild Quest may end early if the number of guild members participating falls below 6, or if the leader decides to end it early!")
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9040011 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9040011(cm: cm))
   }
   return (NPC9040011) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }