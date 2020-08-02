package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest20700 {
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
         if (status == 1) {
            qm.sendNext(I18nMessage.from("20700_REALIZE_HOW_WEAK"))
            qm.dispose()
            return
         }
         status--
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("20700_FINALLY_A_KNIGHT_IN_TRAINING"))
      } else if (status == 1) {
         qm.sendAcceptDecline(I18nMessage.from("20700_HARM_REPUTATION"))
      } else if (status == 2) {
         qm.forceCompleteQuest()
         qm.sendNext(I18nMessage.from("20700_HELP_YOU_TRAIN"))
      } else if (status == 3) {
         qm.sendPrev(I18nMessage.from("20700_GIVE_YOU_BLESSING"))
      } else if (status == 4) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.dispose()
   }
}

Quest20700 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20700(qm: qm))
   }
   return (Quest20700) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}