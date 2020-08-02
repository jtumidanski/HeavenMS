package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

class NPC1092007 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      status = -1
      action((byte) 1, (byte) 0, 0)
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            cm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            if (cm.getQuestStatus(2175) == 1) {
               if (cm.getPlayer().canHold(2030019)) {
                  cm.sendOk(I18nMessage.from("1092007_TAKE_THIS"))
               } else {
                  cm.sendOk(I18nMessage.from("1092007_NO_FREE_INVENTORY"))
                  cm.dispose()
               }
            } else {
               cm.sendOk(I18nMessage.from("1092007_CHASING_ONE_ANOTHER"))
               cm.dispose()
            }
         } else if (status == 1) {
            cm.gainItem(2030019, (short) 1)
            cm.warp(100000006, 0)
            cm.dispose()
         }
      }
   }
}

NPC1092007 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1092007(cm: cm))
   }
   return (NPC1092007) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }