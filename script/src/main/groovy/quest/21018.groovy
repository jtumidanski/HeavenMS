package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21018 {
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
         qm.sendNext(I18nMessage.from("21018_UNDERGO_A_TEST"))
      } else if (status == 1) {
         qm.sendAcceptDecline(I18nMessage.from("21018_HOW_ABOUT_5"))
      } else if (status == 2) {
         if (mode == 0 && type == 15) {
            qm.sendNext(I18nMessage.from("21018_5_IS_NOT_ENOUGH"))
            qm.dispose()
         } else {
            qm.forceStartQuest()
            qm.sendNext(I18nMessage.from("21018_CONTINUE_GOING_LEFT"))
         }
      } else if (status == 3) {
         qm.showInfo("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow1")
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21018 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21018(qm: qm))
   }
   return (Quest21018) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}