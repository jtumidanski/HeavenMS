package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage

class Quest20013 {
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
            qm.sendNext(I18nMessage.from("20013_TOO_MUCH_TO_ASK"))
            qm.dispose()
            return
         }
         status--
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("20013_CLANG_CLANG"))
      } else if (status == 1) {
         qm.sendPrev(I18nMessage.from("20013_WHOA"))
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("20013_BUT_WAIT"))
      } else if (status == 3) {
         qm.sendNextPrev(I18nMessage.from("20013_DO_YOU_KNOW_HOW"))
      } else if (status == 4) {
         qm.sendAcceptDecline(I18nMessage.from("20013_PLEASE_BRING_ME"))
      } else if (status == 5) {
         qm.forceStartQuest()
         qm.guideHint(9)
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || mode == 0 && type > 0) {
         qm.dispose()
         return
      }

      if (mode == 1) {
         status++
      } else {
         status--
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("20013_JUST_WHAT_I_NEED"))
      } else if (status == 1) {
         qm.sendNextPrev(I18nMessage.from("20013_HERE_IT_IS"))
      } else if (status == 2) {
         qm.gainItem(4032267, (short) -1)
         qm.gainItem(4032268, (short) -1)
         qm.gainItem(3010060, (short) 1)
         qm.forceCompleteQuest()
         qm.forceCompleteQuest(20000)
         qm.forceCompleteQuest(20001)
         qm.forceCompleteQuest(20002)
         qm.forceCompleteQuest(20015)
         qm.gainExp(95)
         qm.guideHint(10)
         qm.dispose()
      }
   }
}

Quest20013 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20013(qm: qm))
   }
   return (Quest20013) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}