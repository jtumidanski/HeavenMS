package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage

class Quest20100 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode > 0) {
         status++
      } else {
         qm.dispose()
         return
      }
      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("20100_YOU_ARE_BACK"))
      } else if (status == 1) {
         qm.forceStartQuest()
         qm.forceCompleteQuest()
         qm.sendOk(I18nMessage.from("20100_LOOK_TO_THE_LEFT"))
      } else if (status == 2) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20100 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20100(qm: qm))
   }
   return (Quest20100) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}