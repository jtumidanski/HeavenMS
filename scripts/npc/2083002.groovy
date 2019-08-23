package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2083002 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendYesNo("Do you wish to leave?")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         if (cm.getMapId() > 240050400) {
            cm.warp(240050600)
         } else {
            cm.warp(240040700)
         }

         cm.dispose()
      }
   }
}

NPC2083002 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2083002(cm: cm))
   }
   return (NPC2083002) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }