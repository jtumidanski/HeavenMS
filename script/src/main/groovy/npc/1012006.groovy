package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

class NPC1012006 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      cm.sendSimple(I18nMessage.from("1012006_DO_YOU_HAVE_BUSINESS"))
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         cm.dispose()
      } else if (mode == 0) {
         cm.sendNext(I18nMessage.from("1012006_TOO_BUSY"))
         cm.dispose()
      } else {
         status++
         if (status == 1) {
            if (selection == 0) {
               if (cm.haveItem(4031035)) {
                  cm.sendNext(I18nMessage.from("1012006_GET_THAT_LETTER"))
                  cm.dispose()
               } else {
                  cm.sendYesNo(I18nMessage.from("1012006_WANNA_TRAIN_YOUR_PET"))
               }
            } else {
               cm.sendOk(I18nMessage.from("1012006_ARE_YOU_SURE_YOUVE_MET_MAR"))
               cm.dispose()
            }
         } else if (status == 2) {
            cm.gainItem(4031035, (short) 1)
            cm.sendNext(I18nMessage.from("1012006_OH_HERE"))
            cm.dispose()
         }
      }
   }
}

NPC1012006 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1012006(cm: cm))
   }
   return (NPC1012006) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }