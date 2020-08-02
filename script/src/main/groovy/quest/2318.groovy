package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2318 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendOk(I18nMessage.from("2318_I_UNDERSTAND"))
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline(I18nMessage.from("2318_ONE_MORE_SET"))
      } else if (status == 1) {
         qm.forceStartQuest()
         qm.sendOk(I18nMessage.from("2318_OKAY"))
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
         qm.sendOk(I18nMessage.from("2318_DID_YOU"))
      } else if (status == 1) {
         if (!qm.haveItem(4000499, 50)) {
            qm.sendOk(I18nMessage.from("2318_PLEASE_GATHER_ALL"))
            status = 2
            return
         }
         qm.sendNext(I18nMessage.from("2318_THESE_SHOULD_BE_ENOUGH"))
      } else if (status == 2) {
         qm.sendPrev(I18nMessage.from("2318_GOOD_LUCK"))
      } else if (status == 3) {
         qm.forceCompleteQuest()
         qm.gainExp(11500)
         qm.gainItem(4000499, (short) -50)
         qm.gainItem(2430014, (short) 1)
         qm.dispose()
      }
   }
}

Quest2318 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2318(qm: qm))
   }
   return (Quest2318) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}