package quest


import scripting.quest.QuestActionManager

class Quest21010 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 15 && mode == 0) {
            qm.sendNext("Oh, no need to decline my offer. It's no big deal. It's just a potion. Well, let me know if you change your mind.")
            qm.dispose()
            return
         }
         //status -= 2;
      }

      if (status == 0) {
         qm.sendNext("Hm, what's a human doing on this island? Wait, it's #p1201000#. What are you doing here, #p1201000#? And who's that beside you? Is it someone you know, #p1201000#? What? The hero, you say?")
      } else if (status == 1) {
         qm.sendNextPrev("     #i4001170#")//gms like
      } else if (status == 2) {
         qm.sendNextPrev("Ah, this must be the hero you and your clan have been waiting for. Am I right, #p1201000#? Ah, I knew you weren't just accompanying an average passerby...")
      } else if (status == 3) {
         qm.sendAcceptDecline("Oh, but it seems our hero has become very weak since the Black Magician's curse. It's only makes sense, considering that the hero has been asleep for hundreds of years. #bHere, I'll give you a HP Recovery Potion.#k")
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
            qm.sendNext("You have't drank the potion yet.")
            qm.dispose()
         } else {
            qm.sendNext("We've been digging and digging inside the Ice Cave in the hope of finding a hero, but I never thought I'd actually see the day... The prophecy was true! You were right, #p1201000#! Now that one of the legendary heroes has returned, we have no reason to fear the Black Magician!")
         }
      } else if (status == 1) {
         qm.sendOk("Oh, I've kept you too long. I'm sorry, I got a little carried away. I'm sure the other Penguins feel the same way. I know you're busy, but could you #bstop and talk to the other Penguins#k on your way to town? They would be so honored.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#i2000022# 5 #t2000022#\r\n#i2000023# 5 #t2000023#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 16 exp")
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