package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest20600 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++

      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("20600_YOU_BEEN_SLACKING_OFF"))
      } else if (status == 1) {
         if (mode == 1) {
            qm.forceStartQuest()
         }
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.dispose()
   }
}

Quest20600 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20600(qm: qm))
   }
   return (Quest20600) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}