package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest6032 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

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
            qm.sendNext(I18nMessage.from("6032_ATTEND_MY_CLASS"))
         } else if (status == 1) {
            qm.sendNextPrev(I18nMessage.from("6032_I_WILL_TEACH_YOU"))
         } else if (status == 2) {
            qm.sendNextPrev(I18nMessage.from("6032_AS_AN_EXAMPLE"))
         } else if (status == 3) {
            qm.sendNextPrev(I18nMessage.from("6032_HAND_ME_A_FEE"))
         } else if (status == 4) {
            qm.gainMeso(-10000)

            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest6032 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest6032(qm: qm))
   }
   return (Quest6032) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}