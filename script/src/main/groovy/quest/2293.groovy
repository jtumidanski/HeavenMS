package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2293 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || (mode == 0 && status == 0)) {
         qm.dispose()
         return
      } else if (mode == 0) {
         status--
      } else {
         status++
      }

      if (status == 0) {
         qm.sendNext(I18nMessage.from("2293_DO_YOU_REMEMBER"))
         qm.forceStartQuest()
      } else if (status == 1) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || (mode == 0 && status == 0)) {
         qm.dispose()
         return
      } else if (mode == 0) {
         status--
      } else {
         status++
      }

      if (status == 0) {
         qm.sendSimple("Here, I'll give you some samples. Please listen to them and choose one. Please listen carefully before making your choice.\r\n\
            \t#b#L1# Listen to song No. 1#l \r\n\
            \t#L2# Listen to Song No. 2#l \r\n\
            \t#L3# Listen to Song No. 3#l \r\n\
            \r\n\
            \t#e#L4# Enter the correct song.#l")
      } else if (status == 1) {
         if (selection == 1) {
            qm.playSound("Party1/Failed")
            qm.sendOk(I18nMessage.from("2293_AWKWARD"))
            status = -1
         } else if (selection == 2) {
            qm.playSound("Coconut/Failed")
            qm.sendOk(I18nMessage.from("2293_WAS_IT_THIS"))
            status = -1
         } else if (selection == 3) {
            qm.playSound("quest2293/Die")
            qm.sendOk(I18nMessage.from("2293_YOU_HEARD_IT"))
            status = -1
         } else if (selection == 4) {
            qm.sendGetNumber("Now, please tell me the answer. You only get #bone chance#k, so please choose wisely. Please enter #b1, 2, or 3#k in the window below.\r\n", 1, 1, 3)
         }
      } else if (status == 2) {
         if (selection == 1) {
            qm.sendOk(I18nMessage.from("2293_YOU_DO_NOT_ENJOY_MUSIC"))
         } else if (selection == 2) {
            qm.sendOk(I18nMessage.from("2293_ONE_MORE_CHANCE"))
         } else if (selection == 3) {
            qm.sendOk(I18nMessage.from("2293_THANK_YOU"))
            qm.forceCompleteQuest()
            qm.gainExp(32500)
         } else {
            qm.dispose()
         }
      } else if (status == 3) {
         qm.dispose()
      }
   }
}

Quest2293 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2293(qm: qm))
   }
   return (Quest2293) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}