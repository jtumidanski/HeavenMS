package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest22004 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == 0 && type == 0) {
         status--
      } else if (mode == -1) {
         qm.dispose()
         return
      } else {
         status++
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("22004_I_WAS_WORRIED"))
      } else if (status == 1) {
         qm.sendAcceptDecline(I18nMessage.from("22004_BEFORE_I_GO"))
      } else if (status == 2) {
         if (mode == 0) {//decline
            qm.sendNext(I18nMessage.from("22004_WOULD_HAVE_DONE_IT"))
            qm.dispose()
         } else {
            qm.forceStartQuest()
            qm.sendNext(I18nMessage.from("22004_VERY_NICE_OF_YOU"))
         }
      } else if (status == 3) {
         qm.showInfo("UI/tutorial/evan/6/0")
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == 0 && type == 0) {
         status--
      } else if (mode == -1) {
         qm.dispose()
         return
      } else {
         status++
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("22004_BRING_ALL_THE"))
      } else if (status == 1) {
         if (!qm.isQuestCompleted(22004)) {
            qm.gainItem(3010097, true)
            qm.forceCompleteQuest()
            qm.gainExp(210)
         }
         qm.sendNextPrev(I18nMessage.from("22004_I_MADE_THIS_CHAIR"))
      } else if (status == 2) {
         qm.showInfo("UI/tutorial/evan/7/0")
         qm.dispose()
      }
   }
}

Quest22004 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest22004(qm: qm))
   }
   return (Quest22004) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}