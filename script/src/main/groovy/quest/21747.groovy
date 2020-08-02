package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21747 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
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
            qm.sendAcceptDecline(I18nMessage.from("21747_WHO_WOULD_HAVE_THOUGHT"))
         } else if (status == 1) {
            qm.sendNext(I18nMessage.from("21747_PASSWORD_IS"))
         } else {
            qm.forceStartQuest()
            qm.dispose()
         }
      }
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
            qm.sendNext(I18nMessage.from("21747_YOU_HANDLED_THE_TASK_WELL"))
         } else if (status == 1) {
            qm.sendNext(I18nMessage.from("21747_WHAT_COULD_IT_BE"))
         } else if (status == 2) {
            qm.gainExp(16000)
            qm.forceCompleteQuest()

            qm.dispose()
         }
      }
   }
}

Quest21747 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21747(qm: qm))
   }
   return (Quest21747) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}