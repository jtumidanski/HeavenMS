package npc


import scripting.npc.NPCConversationManager
import tools.I18nMessage

class NPC1012115 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.isQuestNotStarted(20706)) {
         cm.sendNext(I18nMessage.from("1012115_NOTHING_SUSPICIOUS"))
      } else if (cm.isQuestStarted(20706)) {
         cm.forceCompleteQuest(20706)
         cm.sendNext(I18nMessage.from("1012115_COMPLETE"))
      } else if (cm.isQuestCompleted(20706)) {
         cm.sendNext(I18nMessage.from("1012115_ALREADY_COMPLETE"))
      }
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {

   }
}

NPC1012115 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1012115(cm: cm))
   }
   return (NPC1012115) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }