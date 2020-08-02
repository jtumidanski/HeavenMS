package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage

class Quest20016 {
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
         if (status == 8) {
            qm.sendNext(I18nMessage.from("20016_STILL_SOME_QUESTIONS"))
            qm.dispose()
            return
         }
         status--
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("20016_HELLO"))
      } else if (status == 1) {
         qm.sendNextPrev(I18nMessage.from("20016_I_WILL_EXPLAIN_IT_ALL"))
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("20016_ISLAND_CALLED_EREVE"))
      } else if (status == 3) {
         qm.sendNextPrev(I18nMessage.from("20016_YOUNG_EMPRESS"))
      } else if (status == 4) {
         qm.sendNextPrev(I18nMessage.from("20016_FINDING_SIGNS"))
      } else if (status == 5) {
         qm.sendNextPrev(I18nMessage.from("20016_BECOME_SPOILED"))
      } else if (status == 6) {
         qm.sendNextPrev(I18nMessage.from("20016_KNIGHTHOOD"))
      } else if (status == 7) {
         qm.sendNextPrev(I18nMessage.from("20016_HAVE_TO_GET_STRONGER"))
      } else if (status == 8) {
         qm.sendAcceptDecline(I18nMessage.from("20016_CONCLUSION"))
      } else if (status == 9) {
         if (!qm.isQuestStarted(20016)) {
            qm.forceStartQuest()
            qm.gainExp(380)
         }
         qm.sendNext(I18nMessage.from("20016_I_AM_GLAD"))
      } else if (status == 10) {
         qm.sendNextPrev(I18nMessage.from("20016_CANNOT_BE_RECOGNIZED_AS_A_KNIGHT"))
      } else if (status == 11) {
         qm.sendNextPrev(I18nMessage.from("20016_WANTED_SOMEONE_WITH_COURAGE"))
      } else if (status == 12) {
         qm.forceCompleteQuest()
         qm.sendPrev(I18nMessage.from("20016_BECOME_STRONGER"))
      } else if (status == 13) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20016 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20016(qm: qm))
   }
   return (Quest20016) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}