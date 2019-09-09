package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1096010 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.isQuestStarted(2566)) {
         if (!cm.haveItem(4032985)) {
            if (cm.canHold(4032985)) {
               cm.gainItem(4032985, true)
               cm.earnTitle("You found the Ignition Device. Bring it to Cutter.")
            }
         } else {
            cm.earnTitle("You already have the Ignition Device.")
         }
      }
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC1096010 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1096010(cm: cm))
   }
   return (NPC1096010) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }