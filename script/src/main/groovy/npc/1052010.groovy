package npc


import scripting.npc.NPCConversationManager

/*
	NPC Name: 		Shumi
	Map(s): 		
	Description: 	JQ Chest #3
*/


class NPC1052010 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      int[] prizes = [4010001, 4010002, 4010003, 4010004, 4010005, 4010006, 4010007]
      if (cm.isQuestStarted(2057)) {
         cm.gainItem(4031041, (short) 1)
      } else {
         int prizeIndex = ((Math.random() * prizes.length) | 0)
         int prize = prizes[prizeIndex]
         cm.gainItem(prize, (short) 1)
      }
      cm.warp(103000100)
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC1052010 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1052010(cm: cm))
   }
   return (NPC1052010) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }