package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21728 {
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
            if (qm.getQuestProgress(21728, 0) == 0) {
               qm.sendNext(I18nMessage.from("21728_HAVE_NOT_FOUND_IT_YET"))
            } else {
               qm.sendNext(I18nMessage.from("21728_ENTRANCE_IS_BLOCKED"))
               qm.gainExp(200)
               qm.forceCompleteQuest()
            }
         } else if (status == 1) {
            qm.dispose()
         }
      }
   }
}

Quest21728 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21728(qm: qm))
   }
   return (Quest21728) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}