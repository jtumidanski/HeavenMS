package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

class NPC1013104 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.isQuestStarted(22007)) {
         if (!cm.haveItem(4032451)) {
            cm.gainItem(4032451, true)
            cm.sendNext(I18nMessage.from("1013104_EGG_OBTAINED"))
         } else {
            cm.sendNext(I18nMessage.from("1013104_EGG_ALREADY_OBTAINED"))
         }
      } else {
         cm.sendNext(I18nMessage.from("1013104_NO_NEED_FOR_EGG"))
      }
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {
   }
}

NPC1013104 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1013104(cm: cm))
   }
   return (NPC1013104) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }