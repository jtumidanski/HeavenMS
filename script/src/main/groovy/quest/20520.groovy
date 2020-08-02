package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest20520 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            qm.dispose()
            return
         } else {
            qm.dispose()
            return
         }
      }

      if (status == 0) {
         qm.sendNext(I18nMessage.from("20520_WOW_YOU_ALREADY"))
      } else if (status == 1) {
         qm.sendAcceptDecline(I18nMessage.from("20520_RISK_MARRING_THE_PRIDE"))
      } else if (status == 2) {
         qm.forceStartQuest()
         qm.forceCompleteQuest()
         qm.sendOk(I18nMessage.from("20520_SPECIAL_MOUNT"))
      } else if (status == 3) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20520 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20520(qm: qm))
   }
   return (Quest20520) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}