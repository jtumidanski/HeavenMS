package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest22501 {
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
         qm.sendNext(I18nMessage.from("22501_PROVE_TO_ME"))
      } else if (status == 1) {
         qm.sendNextPrev("Eh, I still don't get what's going on, but I can't let a poor little critter like you starve, right? Food, you say? What do you want to eat?", (byte) 2)
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("22501_HELLO"))
      } else if (status == 3) {
         qm.sendAcceptDecline(I18nMessage.from("22501_LEARN_TOGETHER"))
      } else if (status == 4) {
         if (mode == 0) {
            qm.sendNext(I18nMessage.from("22501_GASP"))
         } else {
            qm.forceStartQuest()
            qm.sendOk(I18nMessage.from("22501_EXTREMELY_HUNGRY"))
         }
      } else if (status == 5) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest22501 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest22501(qm: qm))
   }
   return (Quest22501) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}