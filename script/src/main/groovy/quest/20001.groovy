package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage

class Quest20001 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            qm.sendNext(I18nMessage.from("20001_WELCOME_TO_THE_CYGNUS_KNIGHTS"))
         } else if (status == 1) {
            qm.sendNextPrev(I18nMessage.from("20001_I_WILL_EXPLAIN"))
         } else if (status == 2) {
            qm.sendNextPrev(I18nMessage.from("20001_ISLAND_CALLED_EREVE"))
         } else if (status == 3) {
            qm.sendNextPrev(I18nMessage.from("20001_YOUNG_EMPRESS"))
         } else if (status == 4) {
            qm.sendNextPrev(I18nMessage.from("20001_HAVE_TO_TAKE_CONTROL"))
         } else if (status == 5) {
            qm.sendNextPrev(I18nMessage.from("20001_PROBLEM_IS"))
         } else if (status == 6) {
            qm.sendNextPrev(I18nMessage.from("20001_GROUP_OF_KNIGHTS"))
         } else if (status == 7) {
            qm.sendNextPrev(I18nMessage.from("20001_DUTIES_ARE_SIMPLE"))
         } else if (status == 8) {
            qm.sendAcceptDecline(I18nMessage.from("20001_UNDERSTOOD"))
         } else if (status == 9) {
            if (qm.isQuestCompleted(20001)) {
               qm.gainExp(40)
               qm.gainItem(1052177, (short) 1) // fancy noblesse robe
            }
            qm.forceStartQuest()
            qm.forceCompleteQuest()
            qm.sendNext(I18nMessage.from("20001_I_AM_GLAD"))
         } else if (status == 10) {
            qm.sendNextPrev(I18nMessage.from("20001_A_LOT_OF_TIME_WILL_PASS"))
         } else if (status == 11) {
            qm.sendNextPrev(I18nMessage.from("20001_NO_ONE_IS_BORN_STRONG"))
         } else if (status == 12) {
            qm.sendPrev(I18nMessage.from("20001_HEAD_TOWARDS_THE_TRAINING_FOREST"))
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20001 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20001(qm: qm))
   }
   return (Quest20001) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}