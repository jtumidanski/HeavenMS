package quest

import client.MapleJob
import scripting.quest.QuestActionManager
import tools.I18nMessage

class Quest20101 {
   QuestActionManager qm
   int status = -1

   int jobType = 1

   boolean canTryFirstJob = true

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == 0) {
         if (status == 0) {
            qm.sendNext(I18nMessage.from("20101_IMPORTANT_DECISION"))
            qm.dispose()
            return
         }
         status--
      } else {
         status++
      }
      if (status == 0) {
         qm.sendYesNo(I18nMessage.from("20101_DO_YOU_WANT_TO_BECOME"))
      } else if (status == 1) {
         if (canTryFirstJob) {
            canTryFirstJob = false
            if (qm.getPlayer().getJob().getId() != 1100) {
               if(!qm.canGetFirstJob(jobType)) {
                  qm.sendOk(I18nMessage.from("20101_TRAIN_A_BIT_MORE").with(qm.getFirstJobStatRequirement(jobType)))
                  qm.dispose()
                  return
               }

               if (!(qm.canHoldAll([1302077, 1142066]))) {
                  qm.sendOk(I18nMessage.from("20101_MAKE_SOME_ROOM"))
                  qm.dispose()
                  return
               }

               qm.gainItem(1302077, (short) 1)
               qm.gainItem(1142066, (short) 1)
               qm.changeJob(MapleJob.DAWN_WARRIOR_1)
               qm.getPlayer().resetStats()
            }
            qm.forceCompleteQuest()
         }

         qm.sendNext(I18nMessage.from("20101_YOU_ARE_A_DAWN_WARRIOR"))
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("20101_EXPANDED_INVENTORY"))
      } else if (status == 3) {
         qm.sendNextPrev(I18nMessage.from("20101_GIVEN_SP"))
      } else if (status == 4) {
         qm.sendNextPrev(I18nMessage.from("20101_LOSE_EXP_WHEN_YOU_DIE"))
      } else if (status == 5) {
         qm.sendNextPrev(I18nMessage.from("20101_SHOW_THE_WORLD"))
      } else if (status == 6) {
         qm.dispose()
      }
   }
}

Quest20101 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20101(qm: qm))
   }
   return (Quest20101) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}