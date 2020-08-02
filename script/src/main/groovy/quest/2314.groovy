package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2314 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendNext(I18nMessage.from("2314_DO_NOT_LOSE_FAITH"))
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("2314_IN_ORDER_TO"))
      } else if (status == 1) {
         qm.sendNext(I18nMessage.from("2314_BARRIER"))
      } else if (status == 2) {
         //qm.forceStartQuest();
         //qm.forceStartQuest(2314,"1");
         qm.gainExp(8300)
         qm.sendOk(I18nMessage.from("2314_GREAT_WORK"))
         qm.forceCompleteQuest()
      } else if (status == 3) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendOk(I18nMessage.from("2314_WHAT_WAS_IT_LIKE"))
      }
      if (status == 1) {
         qm.forceCompleteQuest()
         qm.gainExp(8300)
         qm.sendOk(I18nMessage.from("2314_GREAT_WORK"))
      } else if (status == 2) {
         qm.dispose()
      }
   }
}

Quest2314 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2314(qm: qm))
   }
   return (Quest2314) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}