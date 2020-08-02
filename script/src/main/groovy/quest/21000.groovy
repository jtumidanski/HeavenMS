package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21000 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendNext(I18nMessage.from("21000_CANNOT_LEAVE_A_KID_BEHIND"))
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("21000_STILL_A_CHILD_IN_THE_FOREST"))
      } else if (status == 1) {
         qm.forceStartQuest()
         qm.sendNext(I18nMessage.from("21000_PROBABLY_LOST"))
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("21000_DO_NOT_PANIC"))
      } else if (status == 3) {
         qm.sendNextPrev(I18nMessage.from("21000_I_AM_BEGGING_YOU"))
      } else if (status == 4) {
         qm.showInfo("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow1")
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.forceCompleteQuest()
      qm.dispose()
   }
}

Quest21000 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21000(qm: qm))
   }
   return (Quest21000) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}