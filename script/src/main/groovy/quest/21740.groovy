package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21740 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
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
            qm.sendNext(I18nMessage.from("21740_ORBIS_SEAL_STOLEN"))
         } else {
            qm.forceStartQuest()
            qm.dispose()
         }
      }
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
            qm.sendNext(I18nMessage.from("21740_UNCOVERED_A_LOST_SKILL"))
         } else if (status == 1) {
            qm.sendNext(I18nMessage.from("21740_LET_ME_TEACH_YOU"))
         } else if (status == 2) {
            qm.forceCompleteQuest()
            qm.teachSkill(21100004, (byte) 0, (byte) 20, -1) // combo smash
            qm.dispose()
         }
      }
   }
}

Quest21740 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21740(qm: qm))
   }
   return (Quest21740) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}