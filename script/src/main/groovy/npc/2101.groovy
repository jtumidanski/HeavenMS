package npc


import scripting.npc.NPCConversationManager

/*



*/


class NPC2101 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendYesNo("Are you done with your training? If you wish, I will send you out from this training camp.")
   }

   def action(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0) {
            cm.sendOk("Haven't you finished the training program yet? If you want to leave this place, please do not hesitate to tell me.")
         }
         cm.dispose()
         return
      }
      if (status == 0) {
         cm.sendNext("Then, I will send you out from here. Good job.")
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