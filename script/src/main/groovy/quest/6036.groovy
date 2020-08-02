package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest6036 {
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
            qm.sendNext(I18nMessage.from("6036_BOTHERING_ME_AGAIN"))
         } else if (status == 1) {
            if (qm.haveItem(4031980, 1)) {
               qm.sendNext(I18nMessage.from("6036_HOW_DID_YOU_DO_IT"))
            } else {
               qm.sendNext(I18nMessage.from("6036_STEP_ASIDE"))
               qm.dispose()
            }
         } else if (status == 2) {
            qm.forceCompleteQuest()
            qm.gainItem(4031980, (short) -1)
            int skillId = Math.floor(qm.getPlayer().getJob().getId() / 1000).intValue() * 10000000 + 1007
            qm.teachSkill(skillId, (byte) 3, (byte) 3, -1)
            qm.gainExp(300000)
            qm.dispose()
         }
      }
   }
}

Quest6036 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest6036(qm: qm))
   }
   return (Quest6036) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}