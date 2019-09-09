package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPCMaybeItsGrendel_end {
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
         cm.sendNext("...a black shadowy figure came out and attacked you? How can this take place at #b#p1032001##k's house? This sounds like one big conspiracy here...")
      } else if (status == 1) {
         cm.sendNextPrev("I'll have to sort this all out in my mind. Talk to me in a bit.")
      } else if (status == 2) {
         cm.dispose()
      }
   }
}

NPCMaybeItsGrendel_end getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPCMaybeItsGrendel_end(cm: cm))
   }
   return (NPCMaybeItsGrendel_end) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }