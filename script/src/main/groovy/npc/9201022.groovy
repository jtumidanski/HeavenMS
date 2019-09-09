package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201022 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.getPlayer().getMapId() == 100000000) {
         cm.sendYesNo("I can take you to the Amoria Village. Are you ready to go?")
      } else {
         cm.sendYesNo("I can take you back to Henesys. Are you ready to go?")
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0) {
            cm.sendOk("Ok, feel free to hang around until you're ready to go!")
         }
         cm.dispose()
         return
      }
      if (status == 0) {
         cm.sendNext("I hope you had a great time! See you around!")
      } else if (status == 1) {
         if (cm.getPlayer().getMapId() == 100000000) {
            cm.warp(680000000, 0)
         } else {
            cm.warp(100000000, 5)
         }
         cm.dispose()
      }
   }
}

NPC9201022 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201022(cm: cm))
   }
   return (NPC9201022) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }