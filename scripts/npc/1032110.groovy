package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1032110 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || (mode == 0 && status == 0)) {
         cm.dispose()
         return
      } else if (mode == 0) {
         status--
      } else {
         status++
      }

      if (status == 0) {
         cm.sendOk("Nothing remarkable here.")
      } else if (status == 1) {
         cm.dispose()
      }
   }
}

NPC1032110 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1032110(cm: cm))
   }
   return (NPC1032110) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }