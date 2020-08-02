package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest20710 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
         return
      } else if (mode == 0 && status == 0) {
         qm.sendOk(I18nMessage.from("20710_DECLINING_THE_MISSION"))
         qm.dispose()
         return
      } else if (mode == 0) {
         status--
      } else {
         status++
      }


      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("20710_DO_NOT_INSTILL_CONFIDENCE"))
      } else if (status == 1) {
         qm.forceStartQuest()
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20710 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20710(qm: qm))
   }
   return (Quest20710) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}