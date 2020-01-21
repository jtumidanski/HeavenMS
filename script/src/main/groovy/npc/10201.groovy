package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

/*

*/

class NPC10201 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendNext(I18nMessage.from("10201_MAGICIAN_INTRO"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0) {
            cm.sendNext(I18nMessage.from("10201_DEMO_NOTE"))
         }
         cm.dispose()
         return
      }
      if (status == 0) {
         cm.sendYesNo(I18nMessage.from("10201_DEMO_PROMPT"))
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