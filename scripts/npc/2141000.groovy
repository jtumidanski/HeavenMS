package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2141000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendAcceptDecline("If only I had the Mirror of Goodness then I can re-summon the Black Wizard! \r\nWait! something's not right! Why is the Black Wizard not summoned? Wait, what's this force? I feel something... totally different from the Black Wizard Ahhhhh!!!!! \r\n\r\n #b(Places a hand on the shoulder of Kryston.)")
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 1) {
         cm.removeNpc(270050100, 2141000)
         cm.forceStartReactor(270050100, 2709000)
      }
      cm.dispose()
   }
}

NPC2141000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2141000(cm: cm))
   }
   return (NPC2141000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }