package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9900001 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.getPlayer().gmLevel() > 1) {
         cm.sendYesNo("Do you want to level up?")
      } else {
         cm.sendOk("Hey wassup?")
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode > 0 && cm.getPlayer().gmLevel() > 1) {
         cm.getPlayer().levelUp(true)
      }
      cm.dispose()
   }
}

NPC9900001 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9900001(cm: cm))
   }
   return (NPC9900001) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }