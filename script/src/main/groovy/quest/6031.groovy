package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest6031 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            qm.sendNext(I18nMessage.from("6031_I_AM_TO_TEACH_YOU"))
         } else if (status == 1) {
            qm.sendNextPrev(I18nMessage.from("6031_SCIENCE_STAGES"))
         } else if (status == 2) {
            qm.sendNextPrev(I18nMessage.from("6031_THIS_MAKES_TRUE"))
         } else if (status == 3) {
            qm.sendNextPrev(I18nMessage.from("6031_TAKE_THAT_IN_MIND"))
         } else if (status == 4) {
            qm.sendNextPrev(I18nMessage.from("6031_THAT_HAS_BEEN_MADE_CLEAR"))
         } else if (status == 5) {
            qm.gainMeso(-10000)

            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest6031 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest6031(qm: qm))
   }
   return (Quest6031) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}