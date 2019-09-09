package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9120200 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendYesNo("Here you are, right in front of the hideout! What? You want to\r\nreturn to #m801000000#?")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0) {
            cm.sendOk("If you want to return to #m801000000#, then talk to me")
            cm.dispose()
         } else if (mode == 1) {
            status++
         }
         if (status == 1) {
            cm.warp(801000000)
            cm.dispose()
         }
      }
   }
}

NPC9120200 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9120200(cm: cm))
   }
   return (NPC9120200) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }