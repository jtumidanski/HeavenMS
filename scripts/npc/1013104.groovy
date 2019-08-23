package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1013104 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.isQuestStarted(22007)) {
         if (!cm.haveItem(4032451)) {
            cm.gainItem(4032451, true)
            cm.sendNext("#b(You have obtained an Egg. Deliver it to Utah.)")
         } else {
            cm.sendNext("#b(You have already obtained an Egg. Take the Egg you have and give it to Utah.)")
         }
      } else {
         cm.sendNext("#b(You don't need to take an egg now.)#k")
      }
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC1013104 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1013104(cm: cm))
   }
   return (NPC1013104) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }