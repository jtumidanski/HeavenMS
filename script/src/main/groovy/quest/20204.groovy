package quest

import client.MapleJob
import scripting.quest.QuestActionManager
import tools.I18nMessage

class Quest20204 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (status == 0 && mode == 0) {
            qm.sendNext(I18nMessage.from("20204_WHAT_IS_HOLDING_YOU_BACK"))
            qm.dispose()
            return
         }
         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            qm.sendYesNo(I18nMessage.from("20204_QUALIFIED"))
         } else if (status == 1) {
            if (qm.getPlayer().getJob().getId() == 1400 && qm.getPlayer().getRemainingSp() > ((qm.getPlayer().getLevel() - 30) * 3)) {
               qm.sendNext(I18nMessage.from("20204_USE_ALL_SP"))
               qm.dispose()
            } else {
               if (qm.getPlayer().getJob().getId() != 1410) {
                  if (!qm.canHold(1142067)) {
                     qm.sendNext(I18nMessage.from("20204_MAKE_INVENTORY_ROOM"))
                     qm.dispose()
                     return
                  }
                  qm.gainItem(4032099, (short) -30)
                  qm.gainItem(1142067, (short) 1)
                  qm.getPlayer().changeJob(MapleJob.NIGHT_WALKER_2)
                  qm.completeQuest()
               }
               qm.sendNext(I18nMessage.from("20204_SUCCESS"))
            }
         } else if (status == 2) {
            qm.sendNextPrev(I18nMessage.from("20204_GIVEN_SP"))
         } else if (status == 3) {
            qm.sendPrev(I18nMessage.from("20204_ACT_LIKE_ONE"))
         } else if (status == 4) {
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest20204 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20204(qm: qm))
   }
   return (Quest20204) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}