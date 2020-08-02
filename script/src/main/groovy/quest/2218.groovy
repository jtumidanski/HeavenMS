package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2218 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
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
            qm.sendNext(I18nMessage.from("2218_SEE_HOW_STRANGE"))
         } else if (status == 1) {
            qm.forceCompleteQuest()
            qm.gainExp(7000)

            if (areAllSubQuestsDone() && qm.haveItem(4031894)) {
               qm.gainItem(4031894, (short) -1)
            }

            qm.dispose()
         }
      }
   }

   def areAllSubQuestsDone() {
      for (int i = 2216; i <= 2219; i++) {
         if (!qm.isQuestCompleted(i)) {
            return false
         }
      }

      return true
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest2218 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2218(qm: qm))
   }
   return (Quest2218) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}