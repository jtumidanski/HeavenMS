package npc


import scripting.npc.NPCConversationManager

class NPC1052009 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      int[] prizes = [4020005, 4020006, 4020007, 4020008, 4010000]
      if (cm.isQuestStarted(2056)) {
         cm.gainItem(4031040, (short) 1)
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

NPC1052009 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1052009(cm: cm))
   }
   return (NPC1052009) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }