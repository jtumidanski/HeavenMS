package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2215 {
   QuestActionManager qm
   int status = -1
   boolean canComplete

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
            int hourDay = qm.getHourOfDay()
            if (!(hourDay >= 17 && hourDay < 20)) {
               qm.sendNext(I18nMessage.from("2215_CANNOT_FIND"))
               canComplete = false
               return
            }

            if (qm.getMeso() < 2000) {
               qm.sendNext(I18nMessage.from("2215_DO_NOT_HAVE_THE_FEE"))
               canComplete = false
               return
            }

            if (!qm.canHold(4031894, 1)) {
               qm.sendNext(I18nMessage.from("2215_NEED_ETC_SLOT"))
               canComplete = false
               return
            }

            canComplete = true
            qm.sendNext(I18nMessage.from("2215_DEPOSIT_THE_FEE"))
         } else if (status == 1) {
            if (canComplete) {
               qm.forceCompleteQuest()
               qm.gainItem(4031894, (short) 1)
               qm.gainMeso(-2000)
            }
            qm.dispose()
         }
      }
   }
}

Quest2215 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2215(qm: qm))
   }
   return (Quest2215) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}