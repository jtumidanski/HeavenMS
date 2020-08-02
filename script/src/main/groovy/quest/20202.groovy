package quest

import client.MapleJob
import scripting.quest.QuestActionManager
import tools.I18nMessage

class Quest20202 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (status == 0 && mode == 0) {
            qm.sendNext(I18nMessage.from("20202_IS_SOMETHING_WRONG"))
            qm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            qm.sendYesNo(I18nMessage.from("20202_QUALIFIED"))
         } else if (status == 1) {
            if (qm.getPlayer().getJob().getId() == 1200 && qm.getPlayer().getRemainingSp() > ((qm.getPlayer().getLevel() - 30) * 3)) {
               qm.sendNext(I18nMessage.from("20202_USE_ALL_SP"))
               qm.dispose()
            } else {
               if (qm.getPlayer().getJob().getId() != 1210) {
                  if (!qm.canHold(1142067)) {
                     qm.sendNext(I18nMessage.from("20202_MAKE_INVENTORY_ROOM"))
                     qm.dispose()
                     return
                  }
                  qm.gainItem(4032097, (short) -30)
                  qm.gainItem(1142067, (short) 1)
                  qm.getPlayer().changeJob(MapleJob.BLAZE_WIZARD_2)
                  qm.completeQuest()
               }
               qm.sendNext(I18nMessage.from("20202_SUCCESS"))
            }
         } else if (status == 2) {
            qm.sendNextPrev(I18nMessage.from("20202_GIVEN_SP"))
         } else if (status == 3) {
            qm.sendPrev(I18nMessage.from("20202_ACT_LIKE_ONE"))
         } else if (status == 4) {
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20202 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20202(qm: qm))
   }
   return (Quest20202) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}