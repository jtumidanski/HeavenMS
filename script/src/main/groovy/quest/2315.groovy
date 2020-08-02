package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2315 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk(I18nMessage.from("2315_DO_NOT_FORGET"))
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("2315_POWERFUL_MAGIC_BARRIER"))
      } else if (status == 1) {
         qm.forceStartQuest()
         qm.sendOk(I18nMessage.from("2315_GO_SEE_HIM"))
      } else if (status == 2) {
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
         qm.sendOk(I18nMessage.from("2315_YOU_INVESTIGATED"))
      } else if (status == 1) {
         qm.forceCompleteQuest()
         qm.gainExp(4000)
         qm.sendOk(I18nMessage.from("2315_INTERESTING"))
      } else if (status == 2) {
         qm.dispose()
      }
   }
}

Quest2315 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2315(qm: qm))
   }
   return (Quest2315) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}