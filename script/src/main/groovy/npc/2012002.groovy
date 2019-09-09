package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2012002 {
   NPCConversationManager cm
   int status = 0
   int sel = -1

   def start() {
      cm.sendYesNo("Do you wish to leave the boat?")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 0 && status == 1) {
         cm.sendOk("Good choice")
         cm.dispose()
      }
      if (mode > 0) {
         status++
      } else {
         cm.dispose()
      }

      if (status == 1) {
         cm.sendNext("Alright, see you next time. Take care.")
      } else if (status == 2) {
         cm.warp(200000111, 0)// back to Orbis jetty
         cm.dispose()
      }
   }
}

NPC2012002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2012002(cm: cm))
   }
   return (NPC2012002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }