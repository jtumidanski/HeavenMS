package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest6033 {
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
            qm.sendNext(I18nMessage.from("6033_LET_US_TAKE_A_LOOK"))
         } else if (status == 1) {
            if (qm.getQuestProgressInt(6033) == 1 && qm.haveItem(4260003, 1)) {
               qm.sendNextPrev(I18nMessage.from("6033_FINE_PIECE"))
            } else {
               qm.sendNext(I18nMessage.from("6033_WHAT_IS_WRONG"))
               qm.dispose()
            }
         } else if (status == 2) {
            qm.forceCompleteQuest()
            int skillId = Math.floor(qm.getPlayer().getJob().getId() / 1000).intValue() * 10000000 + 1007
            qm.teachSkill(skillId, (byte) 2, (byte) 3, -1)
            qm.gainExp(230000)
            qm.dispose()
         }
      }
   }
}

Quest6033 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest6033(qm: qm))
   }
   return (Quest6033) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}