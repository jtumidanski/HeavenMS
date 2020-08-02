package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest20610 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++

      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("20610_TIME_TO_LEARN"))
      } else if (status == 1) {
         if (mode == 0) {
            qm.sendOk(I18nMessage.from("20610_NOT_HUMBLE"))
         } else {
            qm.forceStartQuest()
            qm.dispose()
         }
      } else if (status == 2) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.dispose()
   }
}

Quest20610 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20610(qm: qm))
   }
   return (Quest20610) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}