package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2327 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
         return
      } else if (mode == 0 && status == 0) {
         qm.dispose()
         return
      } else if (mode == 0) {
         status--
      } else {
         status++
      }

      if (status == 0) {
         qm.sendNext(I18nMessage.from("2327_THANK_YOU"))
      } else if (status == 1) {
         qm.sendNextPrev(I18nMessage.from("2327_I_PLAN_TO_ESCAPE"))
      } else if (status == 2) {
         qm.forceStartQuest()
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest2327 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2327(qm: qm))
   }
   return (Quest2327) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}