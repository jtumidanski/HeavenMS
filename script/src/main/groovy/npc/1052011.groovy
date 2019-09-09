package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1052011 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendNext("This device is connected to outside.")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else if (mode == 0) {
         cm.sendOk("Alright, see you next time.")
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            cm.sendNextPrev("Are you going to give up and leave this place?")
         } else if (status == 2) {
            cm.sendYesNo("You'll have to start from scratch the next time you come in...")
         } else if (status == 3) {
            cm.warp(103000100, 0)
            cm.dispose()
         }
      }
   }
}

NPC1052011 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1052011(cm: cm))
   }
   return (NPC1052011) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }