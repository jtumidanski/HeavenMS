package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21700 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || mode == 0 && type > 0) {
         qm.dispose()
         return
      }

      if (mode == 1) {
         status++
      } else {
         if (status == 4) {
            qm.sendNext(I18nMessage.from("21700_TRAIN_WITH_AN_INSTRUCTOR"))
            qm.dispose()
            return
         }
         status--
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("21700_ANYTHING_ELSE_YOU_REMEMBER"))
      } else if (status == 1) {
         qm.sendNextPrev("#b(You tell her that you remember a few skills.)#k", (byte) 2)
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("21700_IT_IS_PROGRESS"))
      } else if (status == 3) {
         qm.sendNextPrev('How do I recover my abilities?', (byte) 2)
      } else if (status == 4) {
         qm.sendAcceptDecline(I18nMessage.from("21700_TRAIN_TRAIN"))
      } else if (status == 5) {
         qm.sendNext(I18nMessage.from("21700_I_GAVE_YOU"))
         if (!qm.isQuestStarted(21700) && !qm.isQuestCompleted(21700)) {
            qm.gainItem(1442000, (short) 1)
            qm.forceStartQuest()
         }
      } else if (status == 6) {
         qm.sendPrev(I18nMessage.from("21700_TRAINING_CENTER_TO_THE_LEFT"))
      } else if (status == 7) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.dispose()
   }
}

Quest21700 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21700(qm: qm))
   }
   return (Quest21700) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}