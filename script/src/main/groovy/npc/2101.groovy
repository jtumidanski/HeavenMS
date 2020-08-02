package npc
import tools.I18nMessage


import scripting.npc.NPCConversationManager

/*



*/


class NPC2101 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendYesNo(I18nMessage.from("2101_DONE_TRAINING"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0) {
            cm.sendOk(I18nMessage.from("2101_IF_YOU_WANT_TO_LEAVE"))
         }
         cm.dispose()
         return
      }
      if (status == 0) {
         cm.sendNext(I18nMessage.from("2101_I_WILL_SEND_YOU_OUT"))
      } else {
         cm.warp(40000, 0)
         cm.dispose()
      }
   }
}

NPC2101 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2101(cm: cm))
   }
   return (NPC2101) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }