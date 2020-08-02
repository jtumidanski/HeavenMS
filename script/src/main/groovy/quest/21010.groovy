package quest
import tools.I18nMessage


import scripting.quest.QuestActionManager

class Quest21010 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 15 && mode == 0) {
            qm.sendNext(I18nMessage.from("21010_NO_NEED_TO_DECLINE"))
            qm.dispose()
            return
         }
         //status -= 2;
      }

      if (status == 0) {
         qm.sendNext(I18nMessage.from("21010_WHATS_A_HUMAN"))
      } else if (status == 1) {
         qm.sendNextPrev("     #i4001170#")//gms like
      } else if (status == 2) {
         qm.sendNextPrev(I18nMessage.from("21010_HAVE_BEEN_WAITING_FOR"))
      } else if (status == 3) {
         qm.sendAcceptDecline(I18nMessage.from("21010_OUR_HERO_HAS_BECOME_VERY_WEAK"))
//nexon probably forgot to remove the '.' before '#k', lol
      } else if (status == 4) {
         if (qm.getPlayer().getHp() >= 50) {
            qm.getPlayer().updateHp(25)
         }
         if (!qm.isQuestStarted(21010) && !qm.isQuestCompleted(21010)) {
            qm.gainItem(2000022, (short) 1)
            qm.forceStartQuest()
         }
         qm.sendNext("Drink it first. Then we'll talk.", (byte) 9)
      } else if (status == 5) {
         qm.sendNextPrev("#b(How do I drink the potion? I don't remember..)", (byte) 3)
      } else if (status == 6) {
         qm.guideHint(14)
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            qm.dispose()
         } else {
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         if (qm.c.getPlayer().getHp() < 50) {
            qm.sendNext(I18nMessage.from("21010_HAVE_NOT_HAD_THE_POTION"))
            qm.dispose()
         } else {
            qm.sendNext(I18nMessage.from("21010_DIGGING_AND_DIGGIN"))
         }
      } else if (status == 1) {
         qm.sendOk(I18nMessage.from("21010_I_HAVE_KEPT_YOU_TOO_LONG"))
      } else if (status == 2) {
         if (qm.isQuestStarted(21010) && !qm.isQuestCompleted(21010)) {
            qm.gainExp(16)
            qm.gainItem(2000022, (short) 3)
            qm.gainItem(2000023, (short) 3)
            qm.forceCompleteQuest()
         }

         qm.sendNext("Oh, you've leveled up! You may have even received some skill points. In Maple World, you can acquire 3 skill points every time you level up. Press the #bK key #kto view the Skill window.", (byte) 9)
      } else if (status == 3) {
         qm.sendNextPrev("#b(Everyone's been so nice to me, but I just can't remember anything. Am I really a hero? I should check my skills and see. But how do I check them?)", (byte) 3)
      } else if (status == 4) {
         qm.guideHint(15)
         qm.dispose()
      }
   }
}

Quest21010 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21010(qm: qm))
   }
   return (Quest21010) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}