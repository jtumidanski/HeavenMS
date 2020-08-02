package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Quest20010 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.sendNext(I18nMessage.from("20010_WHOA_WHOA"))
         qm.dispose()
      } else {
         if(mode == 0 && type > 0) {
            qm.sendNext(I18nMessage.from("20010_WHOA_WHOA"))
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }
         if (status == 0) {
            qm.sendNext(I18nMessage.from("20010_WELCOME_TO_EREVE"))
         } else if (status == 1) {
            qm.sendNextPrev(I18nMessage.from("20010_FIRST_MEET_THE_EMPRESS"))
         } else if (status == 2) {
            qm.sendNextPrev(I18nMessage.from("20010_LET_ME_WARN_YOU"))
         } else if (status == 3) {
            qm.sendAcceptDecline(I18nMessage.from("20010_WOULD_YOU_LIKE_TO_MEET_KIZAN"))
         } else if (status == 4) {
            qm.forceStartQuest()
            qm.guideHint(2)
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         qm.dispose()
      } else {
         if (status == 0) {
            qm.sendOk(I18nMessage.from("20010_NICE_TO_MEET_YOU"))
         } else if (status == 1) {
            if (qm.canHold(2000022) && qm.canHold(2000023)) {
               if (!qm.isQuestCompleted(21010)) {
                  qm.gainItem(2000020, (short) 5)
                  qm.gainItem(2000021, (short) 5)
                  qm.gainExp(15)
               }
               qm.guideHint(3)
               qm.forceCompleteQuest()
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("INVENTORY_FULL_ERROR"))
            }

            qm.dispose()
         }
      }
   }
}

Quest20010 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest20010(qm: qm))
   }
   return (Quest20010) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}