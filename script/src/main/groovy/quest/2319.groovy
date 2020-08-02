package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2319 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk(I18nMessage.from("2319_NOT_A_TOUGH_TASK"))
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("2319_I_ALMOST_FORGOT"))
      } else if (status == 1) {
         qm.forceStartQuest()
         qm.gainItem(4032389, (short) 1)
         qm.sendOk(I18nMessage.from("2319_GIVE_YOU_THE_SAMPLE"))
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
         qm.sendOk(I18nMessage.from("2319_FINALLY_COMPLETED"))
      } else if (status == 1) {
         qm.forceCompleteQuest()
         qm.gainExp(4200)
         qm.gainItem(4032389, (short) -1)
         qm.sendOk(I18nMessage.from("2319_THANK_YOU"))
      } else if (status == 2) {
         qm.dispose()
      }
   }
}

Quest2319 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2319(qm: qm))
   }
   return (Quest2319) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}