package quest

import client.MapleJob
import scripting.quest.QuestActionManager
import tools.I18nMessage

class Quest20104 {
   QuestActionManager qm
   int status = -1

   int jobType = 4

   boolean canTryFirstJob = true

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == 0) {
         if (status == 0) {
            qm.sendNext(I18nMessage.from("20104_IMPORTANT_DECISION"))
            qm.dispose()
            return
         }
         status--
      } else {
         status++
      }
      if (status == 0) {
         qm.sendYesNo(I18nMessage.from("20104_DO_YOU_WANT_TO_BECOME"))
      } else if (status == 1) {
         if (canTryFirstJob) {
            canTryFirstJob = false
            if (qm.getPlayer().getJob().getId() != 1400) {
               if (!qm.canGetFirstJob(jobType)) {
                  qm.sendOk(I18nMessage.from("20104_TRAIN_A_BIT_MORE").with(qm.getFirstJobStatRequirement(jobType)))
                  qm.dispose()
                  return
               }

               if (!(qm.canHoldAll([1472061, 1142066]) && qm.canHold(2070000))) {
                  qm.sendOk(I18nMessage.from("20104_MAKE_SOME_ROOM"))
                  qm.dispose()
                  return
               }

               qm.gainItem(1472061, (short) 1)
               qm.gainItem(2070000, (short) 800)
               qm.gainItem(1142066, (short) 1)
               qm.changeJob(MapleJob.NIGHT_WALKER_1)
               qm.getPlayer().resetStats()
            }
            qm.forceCompleteQuest()
         }

         qm.sendNext(I18nMessage.from("20104_YOU_ARE_A_NIGHT_WALKER"))
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("20104_EXPANDED_INVENTORY"))
      } else if (status == 3) {
         qm.sendNextPrev(I18nMessage.from("20104_GIVEN_SP"))
      } else if (status == 4) {
         qm.sendNextPrev(I18nMessage.from("20104_LOSE_EXP_WHEN_YOU_DIE"))
      } else if (status == 5) {
         qm.sendNextPrev(I18nMessage.from("20104_SHOW_THE_WORLD"))
      } else if (status == 6) {
         qm.dispose()
      }
   }
}

Quest20104 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20104(qm: qm))
   }
   return (Quest20104) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}