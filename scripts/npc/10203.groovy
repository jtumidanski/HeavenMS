package npc


import scripting.npc.NPCConversationManager

/*

*/

class NPC10203 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendNext("Thieves are a perfect blend of luck, dexterity, and power that are adept at the surprise attacks against helpless enemies. A high level of avoidability and speed allows Thieves to attack enemies from various angles.")
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0) {
            cm.sendNext("If you wish to experience what it's like to be a Thief, come see me again.")
         }
         cm.dispose()
         return
      }
      if (status == 0) {
         cm.sendYesNo("Would you like to experience what it's like to be a Thief?")
      } else if (status == 1) {
         cm.lockUI()
         cm.warp(1020400, 0)
         cm.dispose()
      }
   }
}

NPC10203 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC10203(cm: cm))
   }
   return (NPC10203) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }