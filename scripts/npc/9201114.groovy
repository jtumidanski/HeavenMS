package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201114 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.haveItem(3992041, 1)) {
         cm.warp(610030020, "out00")
      } else {
         cm.playerMessage(5, "The giant gate of iron will not budge no matter what, however there is a visible key-shaped socket.")
      }

      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9201114 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201114(cm: cm))
   }
   return (NPC9201114) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }