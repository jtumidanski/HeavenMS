package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage

class Quest20012 {
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
            qm.sendNext(I18nMessage.from("20012_REGULAR_ATTACK"))
            qm.dispose()
            return
         }
         status--
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("20012_I_HAVE_BEEN_WAITING_FOR_YOU"))
      } else if (status == 1) {
         qm.sendNextPrev(I18nMessage.from("20012_EARN_SP"))
      } else if (status == 2) {
         qm.sendAcceptDecline(I18nMessage.from("20012_TIME_TO_PRACTICE"))
      } else if (status == 3) {
         qm.forceStartQuest()
         qm.guideHint(8)
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
         qm.sendNext(I18nMessage.from("20012_VERY_IMPRESSIVE"))
      } else if (status == 1) {
         qm.gainItem(4000483, (short) -1)
         qm.forceCompleteQuest()
         qm.gainExp(40)
         qm.dispose()
      }
   }
}

Quest20012 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20012(qm: qm))
   }
   return (Quest20012) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}