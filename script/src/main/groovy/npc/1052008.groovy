package npc


import scripting.npc.NPCConversationManager

class NPC1052008 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      int[] prizes = [4020000, 4020001, 4020002, 4020003, 4020004]
      if (cm.isQuestStarted(2055)) {
         cm.gainItem(4031039, (short) 1)
      } else {
         int prizeIndex = ((Math.random() * prizes.length) | 0)
         int prize = prizes[prizeIndex]
         cm.gainItem(prize, (short) 1)
      }
      cm.warp(103000100, 0)
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC1052008 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1052008(cm: cm))
   }
   return (NPC1052008) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }