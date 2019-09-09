package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC1043000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      int[] prizes = [1040052, 1040054, 1040130, 1041143, 1042013, 1042022, 1042034, 1042084, 1042098, 1042117, 1702002, 1702015]
      int[] chances = [10, 10, 10, 15, 10, 10, 10, 10, 10, 10, 5, 5]
      int totalOdds = 0
      int choice = 0
      for (def i = 0; i < chances.length; i++) {
         int itemGender = (Math.floor(prizes[i] / 1000) % 10)
         if ((cm.getPlayer().getGender() != itemGender) && (itemGender != 2)) {
            chances[i] = 0
         }
      }
      for (def i = 0; i < chances.length; i++) {
         totalOdds += chances[i]
      }
      int randomPick = Math.floor((Math.random() * totalOdds) + 1).intValue()
      for (def i = 0; i < chances.length; i++) {
         randomPick -= chances[i]
         if (randomPick <= 0) {
            choice = i
            randomPick = totalOdds + 100
         }
      }
      if (cm.isQuestStarted(2050)) {
         cm.gainItem(4031020, (short) 1)
      }
      cm.gainItem(prizes[choice], (short) 1)
      cm.warp(101000000, 0)
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC1043000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1043000(cm: cm))
   }
   return (NPC1043000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }