package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Quest21011 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            qm.sendOk(I18nMessage.from("21011_HEROES_ARE_VERY_BUSY"))
            qm.dispose()
            return
         } else {
            qm.dispose()
            return
         }
      }

      if (status == 0) {
         qm.sendNext(I18nMessage.from("21011_ARE_YOU_THE_HERO"))
      } else if (status == 1) {
         qm.sendNextPrev("   #i4001171#")
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("21011_I_AM_SORRY"))
      } else if (status == 3) {
         qm.sendAcceptDecline(I18nMessage.from("21011_WAIT_A_MINUTE"))
      } else if (status == 4) {
         qm.forceStartQuest()
         qm.sendOk(I18nMessage.from("21011_COULD_YOU_PLEASE_STOP_BY"))
      } else if (status == 5) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            qm.sendNext(I18nMessage.from("21011_GOOD_ENOUGH"))
            qm.dispose()
         } else {
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendNext(I18nMessage.from("21011_ARE_YOU_THE_HERO"))
      } else if (status == 1) {
         qm.sendNextPrev(I18nMessage.from("21011_4001171"))
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("21011_YOU_MUST_BE_SO_HAPPY"))
      } else if (status == 3) {
         qm.sendNextPrev(I18nMessage.from("21011_YOU_ARE_NOT_CARRYING_ANY_WEAPONS"))
      } else if (status == 4) {
         qm.sendYesNo(I18nMessage.from("21011_IT_IS_MY_GIFT_TO_YOU"))
      } else if (status == 5) {
         if (qm.isQuestCompleted(21011)) {
            MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("UNKNOWN_ERROR"))
         } else if (qm.canHold(1302000)) {
            qm.gainItem(1302000, (short) 1)
            qm.gainExp(35)
            qm.forceCompleteQuest()
            qm.sendNext("#b(Your skills are nowhere close to being hero-like... But a sword? Have you ever even held a sword in your lifetime? You can't remember... How do you even equip it?)", (byte) 3)
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("INVENTORY_FULL_ERROR"))
         }
      } else if (status == 6) {
         qm.guideHint(16)
         qm.dispose()
      }
   }
}

Quest21011 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21011(qm: qm))
   }
   return (Quest21011) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}