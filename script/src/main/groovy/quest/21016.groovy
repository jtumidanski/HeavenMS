package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21016 {
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
         qm.sendAcceptDecline(I18nMessage.from("21016_CONTINUE_BASIC_TRAINING"))
      } else if (status == 1) {
         if (mode == 0) {
            qm.sendNext(I18nMessage.from("21016_ONLY_PROCEED_IF_YOU_ARE_READY"))
            qm.dispose()
         } else {
            qm.forceStartQuest()
            qm.sendNext("Alright. This time, let's have you defeat #r#o0100132#s#k, which are slightly more powerful than #o0100131#s. Head over to #b#m140020100##k and defeat #r15#k of them. That should help you build your strength. Alright! Let's do this!", (byte) 1)
         }
      } else if (status == 2) {
         qm.showInfo("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3")
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21016 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21016(qm: qm))
   }
   return (Quest21016) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}