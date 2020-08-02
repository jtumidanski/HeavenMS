package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21300 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode == 0 && type == 0) {
         status -= 2
      } else if (mode != 1) {
         //if (mode == 0)
         qm.sendNext(I18nMessage.from("21300_NEED_TO_THINK_ABOUT_THIS"))
         qm.dispose()
         return
      }

      if (status == 0) {
         qm.sendNext(I18nMessage.from("21300_HOW_IS_THE_TRAINING"))
      } else if (status == 1) {
         qm.sendAcceptDecline(I18nMessage.from("21300_BEFORE_DOING_THAT"))
      } else if (status == 2) {
         qm.forceStartQuest()
         qm.sendOk(I18nMessage.from("21300_ANYWAY"))
      } else if (status == 3) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21300 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21300(qm: qm))
   }
   return (Quest21300) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}