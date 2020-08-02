package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest22502 {
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
         qm.sendAcceptDecline(I18nMessage.from("22502_LIKE_A_COW"))
      } else if (status == 1) {
         if (mode == 0) {
            qm.sendNext(I18nMessage.from("22502_NEVER_KNOW"))
         } else {
            qm.forceStartQuest()
            qm.showInfo("UI/tutorial/evan/12/0")
         }
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest22502 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest22502(qm: qm))
   }
   return (Quest22502) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}