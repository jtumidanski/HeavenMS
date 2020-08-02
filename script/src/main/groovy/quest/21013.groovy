package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class Quest21013 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            qm.sendNext(I18nMessage.from("21013_I_AM_SURE_IT_WILL_COME_IN_HANDY"))
            qm.dispose()
            return
         } else {
            qm.dispose()
            return
         }
      }

      if (status == 0) {
         qm.sendSimple(I18nMessage.from("21013_I_HAVE_BEEN_DYING_TO_MEET_YOU"))
      } else if (status == 1) {
         qm.sendAcceptDecline(I18nMessage.from("21013_I_HAVE_BEEN_WANTING_TO_GIVE_YOU"))
      } else if (status == 2) {
         qm.forceStartQuest()
         qm.sendNext("The parts of the gift have been packed inside a box nearby. Sorry to trouble you, but could you break the box and bring me a #b#t4032309##k and some #b#t4032310##k? I'll assemble them for you right away.", (byte) 9)
      } else if (status == 3) {
         qm.guideHint(18)
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            qm.sendNext(I18nMessage.from("21013_YOU_DO_NOT_WANT_THE_POTION"))
            qm.dispose()
            return
         } else {
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendYesNo(I18nMessage.from("21013_GIVE_ME_A_FEW_SECONDS"))
      } else if (status == 1) {
         if (qm.isQuestCompleted(21013)) {
            MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.POP_UP, I18nMessage.from("UNKNOWN_ERROR"))
         }
         qm.forceCompleteQuest()
         qm.gainExp(95)
         qm.gainItem(4032309, (short) -1)
         qm.gainItem(4032310, (short) -1)
         qm.gainItem(3010062, (short) 1)
         qm.sendNext("Here, a fully-assembled chair, just for you! I've always wanted to give you a chair as a gift, because I know a hero can occasionally use some good rest. Tee hee.", (byte) 9)
      } else if (status == 2) {
         qm.sendNext("A hero is not invincible. A hero is human. I'm sure you will face challenges and even falter at times. But you are a hero because you have what it takes to overcome any obstacles you may encounter.", (byte) 9)
      } else if (status == 3) {
         qm.guideHint(19)
         qm.dispose()
      }
   }
}

Quest21013 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21013(qm: qm))
   }
   return (Quest21013) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}