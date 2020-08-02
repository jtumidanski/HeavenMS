package quest
import tools.I18nMessage

import constants.game.GameConstants
import scripting.quest.QuestActionManager

class Quest2260 {
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
            qm.sendNext(I18nMessage.from("2260_MUSHROOM_CASTLE_INTRO"))
         } else if (status == 1) {
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
            if (GameConstants.getJobBranch(qm.getPlayer().getJob()) == 1) {
               qm.sendNext(I18nMessage.from("2260_2ND_ADVANCEMENT_YET"))
               qm.dispose()
               return
            }

            qm.sendNext(I18nMessage.from("2260_YOU_SEEM_TO_BE_READY"))
            qm.forceCompleteQuest()
         } else if (status == 1) {
            qm.dispose()
         }
      }
   }
}

Quest2260 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2260(qm: qm))
   }
   return (Quest2260) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}