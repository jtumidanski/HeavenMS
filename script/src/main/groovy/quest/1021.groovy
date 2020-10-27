package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Quest1021 {
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
            String token = qm.getPlayer().getGender() == 0 ? "INFORMAL_MALE" : "INFORMAL_FEMALE"
            String i18nToken = I18nMessage.from(token).to(qm.getClient()).evaluate()
            qm.sendNext(I18nMessage.from("1021_HELLO").with(i18nToken))
         } else if (status == 1) {
            qm.sendNextPrev(I18nMessage.from("1021_WHO_MADE_ME"))
         } else if (status == 2) {
            qm.sendAcceptDecline(I18nMessage.from("1021_ABARA"))
         } else if (status == 3) {
            if (qm.getPlayer().getHp() >= 50) {
               qm.getPlayer().updateHp(25)
            }

            if (!qm.haveItem(2010007)) {
               qm.gainItem(2010007, (short) 1)
            }

            qm.forceStartQuest()
            qm.sendNext(I18nMessage.from("1021_SURPRISED"))
         } else if (status == 4) {
            qm.sendPrev(I18nMessage.from("1021_CONSUME_ALL_APPLES"))
         } else if (status == 5) {
            qm.showInfo("UI/tutorial.img/28")
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
            if (qm.c.getPlayer().getHp() < 50) {
               qm.sendNext(I18nMessage.from("1021_DID_YOU_CONSUME_ALL"))
               qm.dispose()
            } else {
               qm.sendNext(I18nMessage.from("1021_HOW_TO"))
            }
         } else if (status == 1) {
            qm.sendNextPrev(I18nMessage.from("1021_PRESENT"))
         } else if (status == 2) {
            qm.sendPrev(I18nMessage.from("1021_ALL_I_CAN_TEACH"))
         } else if (status == 3) {
            if (qm.isQuestCompleted(1021)) {
               MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("UNKNOWN_ERROR"))
            } else if (qm.canHold(2010000) && qm.canHold(2010009)) {
               qm.gainExp(10)
               qm.gainItem(2010000, (short) 3)
               qm.gainItem(2010009, (short) 3)
               qm.forceCompleteQuest()
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("INVENTORY_FULL_ERROR"))
            }
            qm.dispose()
         }
      }
   }
}

Quest1021 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest1021(qm: qm))
   }
   return (Quest1021) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}