package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Quest21012 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 2 && mode == 0) {
            qm.sendOk(I18nMessage.from("21012_THINK_ABOUT_IT"))
            qm.dispose()
            return
         } else {
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("21012_WELCOME"))
      } else if (status == 1) {
         qm.sendNextPrev(I18nMessage.from("21012_TRY_OUT_THE_SWORD"))
      } else if (status == 2) {
         qm.sendAcceptDecline(I18nMessage.from("21012_I_AM_SORRY"))
      } else if (status == 3) {
         qm.forceStartQuest()
         qm.sendNext(I18nMessage.from("21012_IT_JUST_HAPPENS"))
      } else if (status == 4) {
         qm.sendNextPrev(I18nMessage.from("21012_FORGOTTEN_HOW_TO_USE_SKILLS"))
      } else if (status == 5) {
         qm.guideHint(17)
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            qm.sendNext(I18nMessage.from("21012_YOU_DO_NOT_WANT_THE_POTION"))
            qm.dispose()
            return
         } else {
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendOk(I18nMessage.from("21012_THEY_WILL_COME_BACK"))
      } else if (status == 1) {
         if (qm.isQuestCompleted(21012)) {
            MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("UNKNOWN_ERROR"))
         } else if (qm.canHold(2000022) && qm.canHold(2000023)) {
            qm.forceCompleteQuest()
            qm.gainExp(57)
            qm.gainItem(2000022, (short) 10)
            qm.gainItem(2000023, (short) 10)
            qm.sendOk("#b(Even if you're really the hero everyone says you are... What good are you without any skills?)", (byte) 3)
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("INVENTORY_FULL_ERROR"))
            qm.dispose()
         }
      } else if (status == 2) {
         qm.dispose()
      }
   }
}

Quest21012 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21012(qm: qm))
   }
   return (Quest21012) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}