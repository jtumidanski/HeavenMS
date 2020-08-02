package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage

class Quest20020 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.sendOk(I18nMessage.from("20020_DECIDE_WHAT_YOU_REALLY_WANT_TO_DO"))
         qm.dispose()
      } else {
         if (mode == 0 && type > 0 || selection == 1) {
            qm.sendOk(I18nMessage.from("20020_DECIDE_WHAT_YOU_REALLY_WANT_TO_DO"))
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            qm.sendNext(I18nMessage.from("20020_YOU_HAVE_WORKED_HARD"))
         } else if (status == 1) {
            qm.sendNextPrev(I18nMessage.from("20020_FIVE_PATHS"))
         } else if (status == 2) {
            qm.sendSimple(I18nMessage.from("20020_WHAT_DO_YOU_THINK"))
         } else if (status == 3) {
            qm.sendYesNo(I18nMessage.from("20020_SEE_IT_FOR_YOURSELF"))
         } else if (status == 4) {
            qm.forceStartQuest()
            qm.forceCompleteQuest()
            qm.warp(913040100, 0)
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20020 getQuest() {
   QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
   getBinding().setVariable("quest", new Quest20020(qm: qm))
   return (Quest20020) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}