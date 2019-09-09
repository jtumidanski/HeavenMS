package npc


import scripting.npc.NPCConversationManager

/*

*/

class NPC10201 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendNext("Magicians are armed with flashy element-based spells and secondary magic that aids party as a whole. After the 2nd job adv., the elemental-based magic will provide ample amount of damage to enemies of opposite element.")
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0) {
            cm.sendNext("If you wish to experience what it's like to be a Magician, come see me again.")
         }
         cm.dispose()
         return
      }
      if (status == 0) {
         cm.sendYesNo("Would you like to experience what it's like to be a Magician?")
      } else if (status == 1) {
         cm.lockUI()
         cm.warp(1020200, 0)
         cm.dispose()
      }
   }
}

NPC10201 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC10201(cm: cm))
   }
   return (NPC10201) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }