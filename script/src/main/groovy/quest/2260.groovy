package quest

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
            qm.sendNext("Once you've got #b2nd job advancement#k, I'll tell you about the #bMushroom Castle#k.")
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
               qm.sendNext("Eh, didn't you get the #r2nd job advancement#k yet?")
               qm.dispose()
               return
            }

            qm.sendNext("Okay you seem ready to go to the #bMushroom Castle#k. In #rHenesys#k, climb at the tree fort at #bwest#k then enter a portal over there. On the other area, #rgo west#k. From there, a portal will be readily available to access the #bMushroom Castle#k area.")
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