package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest22503 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == 0 && type == 0) {
         status--
      } else if (mode == -1) {
         qm.dispose()
         return
      } else {
         status++
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("22503_THIS_IS_NOT_WHAT_I_NEED"))
      } else if (status == 1) {
         qm.sendNextPrev("#bHm... So you're not a herbivore. You might be a carnivore. You're a Dragon, after all. How does some #t4032453# sound?", (byte) 2)
      } else if (status == 2) {
         qm.sendAcceptDecline(I18nMessage.from("22503_NEVER_HEARD_OF_IT"))
      } else if (status == 3) {
         if (mode == 0) {
            qm.sendNext(I18nMessage.from("22503_HOW_CAN_YOU_STARVE_ME"))
         } else {
            qm.forceStartQuest()
            qm.sendNext(I18nMessage.from("22503_TRY_GIVING"))
         }
      } else if (status == 4) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest22503 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest22503(qm: qm))
   }
   return (Quest22503) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}