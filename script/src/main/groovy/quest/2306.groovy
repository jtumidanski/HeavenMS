package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest2306 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            if (status != 3) {
               qm.sendOk(I18nMessage.from("2306_URGENT_MATTER"))
               qm.dispose()
            } else {
               if (qm.canHold(4032375, 1)) {
                  qm.sendNext(I18nMessage.from("2306_ROUTE"))
               } else {
                  qm.sendOk(I18nMessage.from("2306_ETC_SPACE_NEEDED"))
                  qm.dispose()
               }
            }

            status++
         } else {
            if (mode == 1) {
               status++
            } else {
               status--
            }

            if (status == 0) {
               qm.sendAcceptDecline(I18nMessage.from("2306_READY_FOR_THIS"))
            } else if (status == 1) {
               qm.sendNext(I18nMessage.from("2306_CURRENTLY_IN_DISARRAY"))
            } else if (status == 2) {
               qm.sendNext(I18nMessage.from("2306_SOMETHING_TERRIBLE"))
            } else if (status == 3) {
               qm.sendYesNo(I18nMessage.from("2306_I_CAN_TAKE_YOU_STRAIGHT_THERE"))
            } else if (status == 4) {
               if (qm.canHold(4032375, 1)) {
                  if (!qm.haveItem(4032375, 1)) {
                     qm.gainItem(4032375, (short) 1)
                  }

                  qm.warp(106020000, 0)
                  qm.forceStartQuest()
               } else {
                  qm.sendOk(I18nMessage.from("2306_ETC_SPACE_NEEDED"))
               }

               qm.dispose()
            } else if (status == 5) {
               if (!qm.haveItem(4032375, 1)) {
                  qm.gainItem(4032375, (short) 1)
               }

               qm.forceStartQuest()
               qm.dispose()
            }
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
            if (!qm.haveItem(4032375, 1)) {
               qm.sendNext(I18nMessage.from("2306_WHAT_DO_YOU_WANT"))
               qm.dispose()
               return
            }

            qm.sendNext(I18nMessage.from("2306_RECOMMENDATION_LETTER"))
         } else if (status == 1) {
            qm.sendNextPrev(I18nMessage.from("2306_REALLY_THE_ONE"))
         } else if (status == 2) {
            qm.gainItem(4032375, (short) -1)
            qm.gainExp(6000)
            qm.forceCompleteQuest()
            qm.forceStartQuest(2312)
            qm.dispose()
         }
      }
   }
}

Quest2306 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2306(qm: qm))
   }
   return (Quest2306) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}