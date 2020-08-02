package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21400 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode == 0 && type == 0) {
         status -= 2
      } else if (mode != 1) {
         //if (mode == 0)
         qm.sendNext(I18nMessage.from("21400_NEED_TO_THINK_ABOUT_THIS"))
         qm.dispose()
         return
      }

      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("21400_HOW_IS_TRAINING_GOING"))
      } else if (status == 1) {
         qm.startQuest()
         qm.sendOk(I18nMessage.from("21400_I_HAVE_A_BAD_FEELING"))
      } else if (status == 2) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21400 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21400(qm: qm))
   }
   return (Quest21400) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}