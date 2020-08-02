package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest22003 {
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
         qm.sendAcceptDecline(I18nMessage.from("22003_FORGOT_HIS_LUNCH_BOX"))
      } else if (status == 1) {
         if (mode == 0 && type == 15) {//decline
            qm.sendNext(I18nMessage.from("22003_GOOD_KIDS_LISTEN"))
            qm.dispose()
         } else {
            if (!qm.isQuestStarted(22003)) {
               if (!qm.haveItem(4032448)) {
                  qm.gainItem(4032448, true)
               }
               qm.forceStartQuest()
            }
            qm.sendNext(I18nMessage.from("22003_SUCH_A_GOOD"))
         }
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("22003_COME_BACK_IF_YOU_HAPPEN_TO_LOSE"))
      } else if (status == 3) {
         qm.showInfo("UI/tutorial/evan/5/0")
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest22003 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest22003(qm: qm))
   }
   return (Quest22003) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}