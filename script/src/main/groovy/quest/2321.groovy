package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2321 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk(I18nMessage.from("2321_COME_SEE_ME_WHEN_YOU_ARE_READY"))
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("2321_PLEASE_GO_SEE_HIM"))
      } else if (status == 1) {
         qm.forceStartQuest()
         qm.sendOk(I18nMessage.from("2321_GOOD_LUCK"))
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
         qm.sendOk(I18nMessage.from("2321_CONGRATULATIONS"))
      } else if (status == 1) {
         qm.forceCompleteQuest()
         qm.gainExp(2500)
         qm.sendOk(I18nMessage.from("2321_PROBLEM_NOW"))
      } else if (status == 2) {
         qm.dispose()
      }
   }
}

Quest2321 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2321(qm: qm))
   }
   return (Quest2321) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}