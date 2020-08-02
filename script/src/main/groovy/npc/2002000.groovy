package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

class NPC2002000 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendYesNo(I18nMessage.from("2002000_YOU_WANT_TO_GET_OUT"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode < 1) {
         cm.dispose()
      } else {
         int map = cm.getPlayer().getSavedLocation("HAPPYVILLE")
         if (map == -1) {
            map = 101000000
         }

         cm.warp(map, 0)
      }

      cm.dispose()
   }
}

NPC2002000 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2002000(cm: cm))
   }
   return (NPC2002000) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }