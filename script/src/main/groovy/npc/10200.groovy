package npc


import scripting.npc.NPCConversationManager

/*

*/

class NPC10200 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendNext("Bowmen are blessed with dexterity and power, taking charge of long-distance attacks, providing support for those at the front line of the battle. Very adept at using landscape as part of the arsenal.")
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0) {
            cm.sendNext("If you wish to experience what it's like to be a Bowman, come see me again.")
         }
         cm.dispose()
         return
      }
      if (status == 0) {
         cm.sendYesNo("Would you like to experience what it's like to be a Bowman?")
      } else if (status == 1) {
         cm.lockUI()
         cm.warp(1020300, 0)
         cm.dispose()
      }
   }
}

NPC10200 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC10200(cm: cm))
   }
   return (NPC10200) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }