package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2573 {
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
         qm.sendNext(I18nMessage.from("2573_GREETINGS"))
      } else if (status == 1) {
         qm.sendAcceptDecline(I18nMessage.from("2573_NOT_QUITE_READY"))
      } else if (status == 2) {
         if (mode == 0) {//decline
            qm.sendNext(I18nMessage.from("2573_TAKE_IT_EASY"))
         } else {
            qm.warp(3000000, 0)
            qm.forceCompleteQuest()
            qm.sendNext(I18nMessage.from("2573_WE_ARE_ALL_SET"))
         }
      } else if (status == 3) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest2573 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2573(qm: qm))
   }
   return (Quest2573) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}