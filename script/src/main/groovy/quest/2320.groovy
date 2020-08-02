package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2320 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk(I18nMessage.from("2320_GOOD_NEWS"))
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("2320_ONE_MORE_REQUEST"))
      } else if (status == 1) {
         qm.forceStartQuest()
         qm.gainItem(4032389, (short) 1)
         qm.sendOk(I18nMessage.from("2320_DO_YOU_REMEMBER"))
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
         qm.sendOk(I18nMessage.from("2320_OH"))
      } else if (status == 1) {
         qm.forceCompleteQuest()
         qm.gainExp(8800)
         qm.gainItem(4032389, (short) -1)
         qm.sendOk(I18nMessage.from("2320_PLEASE_TELL"))
      } else if (status == 2) {
         qm.dispose()
      }
   }
}

Quest2320 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2320(qm: qm))
   }
   return (Quest2320) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}