package npc


import scripting.npc.NPCConversationManager

/*

*/

class NPC10202 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendNext("Warriors possess an enormous power with stamina to back it up, and they shine the brightest in melee combat situation. Regular attacks are powerful to begin with, and armed with complex skills, the job is perfect for explosive attacks.")
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0) {
            cm.sendNext("If you wish to experience what it's like to be a Warrior, come see me again.")
         }
         cm.dispose()
         return
      }
      if (status == 0) {
         cm.sendYesNo("Would you like to experience what it's like to be a Warrior?")
      } else if (status == 1) {
         cm.lockUI()
         cm.warp(1020100, 0)
         cm.dispose()
      }
   }
}

NPC10202 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC10202(cm: cm))
   }
   return (NPC10202) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }