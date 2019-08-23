package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1013001 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 0 && type == 0) {
         status--
      } else if (mode == -1) {
         cm.dispose()
         return
      } else {
         status++
      }
      if (status == 0) {
         cm.sendNext("You, who are destined to be a Dragon Master... You have finally arrived.", (byte) 1)
      } else if (status == 1) {
         cm.sendNextPrev("Go and fulfill your duties as the Dragon Master...", (byte) 1)
      } else if (status == 2) {
         cm.warp(900090101, 0)
         cm.dispose()
      }
   }
}

NPC1013001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1013001(cm: cm))
   }
   return (NPC1013001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }