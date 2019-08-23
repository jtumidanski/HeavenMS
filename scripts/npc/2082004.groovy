package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2082004 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            cm.sendOk("Hi, I am Andy, the time traveler from a not so distant future. I have come to avert the creation of machines by the greedy people of this time. They went berserk on my time and consumed everything to dust. I must stop it at any cost!")
            cm.dispose()
         }
      }
   }
}

NPC2082004 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2082004(cm: cm))
   }
   return (NPC2082004) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }