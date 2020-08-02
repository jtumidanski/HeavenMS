package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21704 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || mode == 0 && type > 0) {
         qm.dispose()
         return
      }

      if (mode == 1) {
         status++
      } else {
         if (status == 2) {
            qm.dispose()
            return
         }
         status--
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("21704_HOW_DID_THE_TRAINING_GO"))
      } else if (status == 1) {
         qm.sendNextPrev("#b(You tell her that you were able to remember the Combo Ability skill.)#k", (byte) 2)
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("21704_THAT_IS_GREAT"))
      } else if (status == 3) {
         qm.forceCompleteQuest()
         qm.gainExp(500)
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.dispose()
   }
}

Quest21704 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21704(qm: qm))
   }
   return (Quest21704) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}