package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest3507 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            if (qm.isQuestCompleted(3523) || qm.isQuestCompleted(3524) || qm.isQuestCompleted(3525) || qm.isQuestCompleted(3526) || qm.isQuestCompleted(3527) || qm.isQuestCompleted(3529) || qm.isQuestCompleted(3539)) {
               qm.completeQuest()
               qm.sendOk(I18nMessage.from("3507_NOW_FILLED_WITH_MEMORIES"))
            } else {
               qm.sendOk(I18nMessage.from("3507_CHECK_WITH_TEACHER"))
            }
         } else if (status == 1) {
            qm.dispose()
         }
      }
   }
}

Quest3507 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3507(qm: qm))
   }
   return (Quest3507) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}