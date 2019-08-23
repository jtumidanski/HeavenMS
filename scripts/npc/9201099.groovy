package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201099 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.getQuestStatus(8224) == 2) {
         cm.openShopNPC(9201099)
      } else {
         cm.sendOk("Hm, at who do you think you are looking at?")
      }

      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC9201099 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201099(cm: cm))
   }
   return (NPC9201099) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }