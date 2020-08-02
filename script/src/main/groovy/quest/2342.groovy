package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2342 {
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
            if (!qm.hasItem(4001318) && qm.isQuestStarted(2331) && !qm.isQuestCompleted(2331)) {
               if (qm.canHold(4001318)) {
                  qm.forceStartQuest()
                  qm.gainItem(4001318, (short) 1)
                  qm.forceCompleteQuest()
                  qm.sendOk(I18nMessage.from("2342_LOOKS_LIKE_YOU_FORGOT"))
               } else {
                  qm.sendOk(I18nMessage.from("2342_ETC_SPACE_NEEDED"))
               }
            } else {
               qm.dispose()
            }
         } else if (status == 1) {
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest2342 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2342(qm: qm))
   }
   return (Quest2342) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}