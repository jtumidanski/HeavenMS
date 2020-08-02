package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21749 {
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
            qm.sendNext(I18nMessage.from("21749_LOST_TWO_SEAL_STONES"))
         } else if (status == 1) {
            qm.sendNext(I18nMessage.from("21749_RETRIEVE_LONG_LOST_SEAL_STONE"))
         } else if (status == 2) {
            qm.forceCompleteQuest()
            qm.gainExp(500)
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21749 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21749(qm: qm))
   }
   return (Quest21749) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}