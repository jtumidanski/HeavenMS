package npc


import scripting.npc.NPCConversationManager

/*

*/

class NPC10204 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendNext("Pirates are blessed with outstanding dexterity and power, utilizing their guns for long-range attacks while using their power on melee combat situations. Gunslingers use elemental-based bullets for added damage, while Infighters transform to a different being for maximum effect.")
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0) {
            cm.sendNext("If you wish to experience what it's like to be a Pirate, come see me again.")
         }
         cm.dispose()
         return
      }
      if (status == 0) {
         cm.sendYesNo("Would you like to experience what it's like to be a Pirate?")
      } else if (status == 1) {
         cm.lockUI()
         cm.warp(1020500, 0)
         cm.dispose()
      }
   }
}

NPC10204 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC10204(cm: cm))
   }
   return (NPC10204) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }