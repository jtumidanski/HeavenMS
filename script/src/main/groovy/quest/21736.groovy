package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21736 {
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
            qm.sendNext(I18nMessage.from("21736_LONG_TIME_NO_SEE"))
         } else if (status == 1) {
            qm.sendNextPrev(I18nMessage.from("21736_ENOUGH_SMALL_TALK"))
         } else if (status == 2) {
            qm.sendNextPrev(I18nMessage.from("21736_SOMETHING_STRANGE_IS_HAPPENING"))
         } else if (status == 3) {
            qm.sendAcceptDecline(I18nMessage.from("21736_GO_SEE_LISA_FIRST"))
         } else if (status == 4) {
            qm.forceStartQuest()
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21736 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21736(qm: qm))
   }
   return (Quest21736) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}