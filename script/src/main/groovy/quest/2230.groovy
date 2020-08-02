package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2230 {
   QuestActionManager qm
   int status = -1
   boolean canComplete

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            qm.sendNext(I18nMessage.from("2230_GUARD_IT"))
         } else if (status == 1) {
            qm.sendYesNo(I18nMessage.from("2230_FOLLOW_THE_FORCE"))
         } else if (status == 2) {
            qm.sendOk(I18nMessage.from("2230_PUT_YOUR_HAND_IN_YOUR_POCKET"))
            qm.forceStartQuest()
            qm.gainItem(4032086, (short) 1) // Mysterious Egg * 1
         } else if (status == 3) {
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            qm.sendSimple(I18nMessage.from("2230_HELLO"))
         } else if (selection == 0 && status == 1) {
            qm.sendNext(I18nMessage.from("2230_HAVE_YOU_FOUND"))
         } else if (status == 2) {
            qm.sendNextPrev(I18nMessage.from("2230_RAISING_A_PET"))
         } else if (status == 3) {
            qm.sendNextPrev(I18nMessage.from("2230_I_WANT_TO_INSTAILL_THIS_IN_YOU"))
         } else if (status == 4) {
            qm.sendNextPrev(I18nMessage.from("2230_PET_OF_MANY_SKILLS"))
         } else if (status == 5) {
            qm.sendYesNo(I18nMessage.from("2230_NOW_DO_YOU_UNDERSTAND"))
         } else if (status == 6) {
            canComplete = qm.canHold(5000054, 1)
            if (!canComplete) {
               qm.sendNext(I18nMessage.from("2230_FREE_A_CASH_SLOT"))
               return
            }
            qm.sendNext(I18nMessage.from("2230_SHOWER_IT_WITH_LOVE"))
         } else if (status == 7) {
            if (canComplete) {
               qm.gainItem(4032086, (short) -1) // Mysterious Egg * -1
               qm.forceCompleteQuest()
               qm.gainItem(5000054, (short) 1, false, true, 5 * 60 * 60 * 1000)
            }

            qm.dispose()
         }
      }
   }
}

Quest2230 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2230(qm: qm))
   }
   return (Quest2230) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}