package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage

class Quest20011 {
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
         if (status == 2) {
            qm.sendNext(I18nMessage.from("20011_YOU_DO_NOT_WANT_TO"))
            qm.dispose()
            return
         }
         status--
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("20011_NUMBER_OF_WAYS_TO_HUNT"))
      } else if (status == 1) {
         qm.sendPrev(I18nMessage.from("20011_REGULAR_ATTACK"))
      } else if (status == 2) {
         qm.sendAcceptDecline(I18nMessage.from("20011_GO_TEST_IT_OUT"))
      } else if (status == 3) {
         qm.forceStartQuest()
         qm.guideHint(4)
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || mode == 0 && type > 0) {
         qm.dispose()
         return
      }

      if (mode == 1) {
         status++
      } else {
         status--
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("20011_PRETTY_SIMPLE"))
      } else if (status == 1) {
         qm.sendNextPrev(I18nMessage.from("20011_NOBLESSE_EQUIPMENT"))
      } else if (status == 2) {
         qm.gainItem(1002869, (short) 1)
         qm.gainItem(1052177, (short) 1)
         qm.forceCompleteQuest()
         qm.gainExp(30)
         qm.guideHint(6)
         qm.dispose()
      }
   }
}

Quest20011 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20011(qm: qm))
   }
   return (Quest20011) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}