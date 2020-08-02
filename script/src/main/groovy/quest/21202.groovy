package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21202 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0 && type == 1) {
            qm.sendNext(I18nMessage.from("21202_PUT_IN_THE_WORK"))
         }
         qm.dispose()
         return
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("21202_WHAT_ARE_YOU_DOING_HERE"))
      } else if (status == 1) {
         qm.sendNextPrev("I've come to get the best Polearm there is!", (byte) 2)
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("21202_BEST_POLEARM"))
      } else if (status == 3) {
         qm.sendNextPrev("I hear you are the best blacksmith in all of Maple World! I want nothing less than a weapon made by you!",    (byte) 2)
      } else if (status == 4) {
         qm.sendAcceptDecline(I18nMessage.from("21202_TOO_OLD"))
      } else if (status == 5) {
         qm.sendOk(I18nMessage.from("21202_IF_YOU_SAY_SO"))
      } else if (status == 6) {
         qm.startQuest()
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0 && type == 1) {
            qm.sendNext(I18nMessage.from("21202_IT_WILL_BE_YOURS_IN_THE_END"))
         }
         qm.dispose()
         return
      }
      if (status == 0) {
         if (qm.haveItem(4032311, 30)) {
            qm.sendNext(I18nMessage.from("21202_STRONG_THAN_I_THOUGHT"))
         } else {
            qm.sendNext(I18nMessage.from("21202_GO_FOR_THE_30"))
            qm.dispose()
         }
      } else if (status == 1) {
         qm.sendNextPrev(I18nMessage.from("21202_HANDED_YOU"))
      } else if (status == 2) {
         qm.sendYesNo(I18nMessage.from("21202_HERE_THIS_IS"))
      } else if (status == 3) {
         //qm.showVideo("Polearm");
         qm.completeQuest()
         qm.removeAll(4032311)
         qm.dispose()
      }
   }
}

Quest21202 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21202(qm: qm))
   }
   return (Quest21202) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}