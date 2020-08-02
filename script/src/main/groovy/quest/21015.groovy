package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21015 {
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
         qm.sendNext(I18nMessage.from("21015_I_HAVE_DONE_ENOUGH_EXPLAINING"))
      } else if (status == 1) {
         qm.sendNextPrev(I18nMessage.from("21015_YOU_MAY_HAVE_BEEN"))
      } else if (status == 2) {
         qm.sendAcceptDecline(I18nMessage.from("21015_FIRST_MASTER_THE_FUNDAMENTALS"))
      } else if (status == 3) {
         if (mode == 0) {
            qm.sendNext(I18nMessage.from("21015_YOU_ARE_A_HERO"))
            qm.dispose()
         } else {
            qm.forceStartQuest()
            qm.sendNext("The population of Rien may be mostly Penguins, but even this island has monsters. You'll find #o0100131#s if you go to #b#m140020000##k, located on the right side of the town. Please defeat #r10 of those #o0100131#s#k. I'm sure you'll have no trouble defeating the #o0100131#s that even the slowest penguins here can defeat.", (byte) 1)
         }
      } else if (status == 4) {
         qm.showInfo("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3")
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21015 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21015(qm: qm))
   }
   return (Quest21015) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}