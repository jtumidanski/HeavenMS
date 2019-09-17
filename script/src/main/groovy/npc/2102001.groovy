package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2102001 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (status == 0) {
         cm.sendYesNo("Do you wish to leave the genie?")
         status++
      } else {
         if (mode < 1) {
            cm.dispose()
         } else {
            if (status == 1) {
               cm.sendNext("Alright, see you next time. Take care.")
               status++
            } else if (status == 2) {
               cm.warp(260000100, 0)
               cm.dispose()
            }
         }
      }
   }
}

NPC2102001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2102001(cm: cm))
   }
   return (NPC2102001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }