package quest

import client.MapleJob
import scripting.quest.QuestActionManager
import tools.I18nMessage

class Quest20105 {
   QuestActionManager qm
   int status = -1

   int jobType = 5

   boolean canTryFirstJob = true

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == 0) {
         if (status == 0) {
            qm.sendNext(I18nMessage.from("20105_IMPORTANT_DECISION"))
            qm.dispose()
            return
         }
         status--
      } else {
         status++
      }
      if (status == 0) {
         qm.sendYesNo(I18nMessage.from("20105_DO_YOU_WANT_TO_BECOME"))
      } else if (status == 1) {
         if (canTryFirstJob) {
            canTryFirstJob = false
            if (qm.getPlayer().getJob().getId() != 1500) {
               if (!qm.canGetFirstJob(jobType)) {
                  qm.sendOk(I18nMessage.from("20105_TRAIN_A_BIT_MORE").with(qm.getFirstJobStatRequirement(jobType)))
                  qm.dispose()
                  return
               }

               if (!(qm.canHoldAll([1482014, 1142066]))) {
                  qm.sendOk(I18nMessage.from("20105_MAKE_SOME_ROOM"))
                  qm.dispose()
                  return
               }

               qm.gainItem(1482014, (short) 1)
               qm.gainItem(1142066, (short) 1)
               qm.getPlayer().changeJob(MapleJob.THUNDER_BREAKER_1)
               qm.getPlayer().resetStats()
            }
            qm.forceCompleteQuest()
         }

         qm.sendNext(I18nMessage.from("20105_YOU_ARE_A_THUNDER_BREAKER"))
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("20105_EXPANDED_INVENTORY"))
      } else if (status == 3) {
         qm.sendNextPrev(I18nMessage.from("20105_GIVEN_SP"))
      } else if (status == 4) {
         qm.sendNextPrev(I18nMessage.from("20105_LOSE_EXP_WHEN_YOU_DIE"))
      } else if (status == 5) {
         qm.sendNextPrev(I18nMessage.from("20105_SHOW_THE_WORLD"))
      } else if (status == 6) {
         qm.dispose()
      }
   }
}

Quest20105 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20105(qm: qm))
   }
   return (Quest20105) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}