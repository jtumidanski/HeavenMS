package quest


import scripting.quest.QuestActionManager

class Quest2314 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendNext("Please do not lose faith in our Kingdom of Mushroom.")
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendAcceptDecline("In order to rescue the princess, you must first navigate the Mushroom Forest. King Pepe set up a powerful barrier forbidding anyone from entering the castle. Please investigate this matter for us.")
      } else if (status == 1) {
         qm.sendNext("You'll run into the barrier at the Mushroom Forest by heading east of where you are standing right now. Please be careful. I hear that the area is infested with crazy, fear-inducing monsters.")
      } else if (status == 2) {
         //qm.forceStartQuest();
         //qm.forceStartQuest(2314,"1");
         qm.gainExp(8300)
         qm.sendOk("I see, so it was indeed not a regular barrier by any means. Great work there. If not for you help, we wouldn't have had a clue as to what that was all about.")
         qm.forceCompleteQuest()
      } else if (status == 3) {
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendOk("I see that you have thoroughly investigated the barrier at the Mushroom Forest. What was it like?")
      }
      if (status == 1) {
         qm.forceCompleteQuest()
         qm.gainExp(8300)
         qm.sendOk("I see, so it was indeed not a regular barrier by any means. Great work there. If not for you help, we wouldn't have had a clue as to what that was all about.")
      } else if (status == 2) {
         qm.dispose()
      }
   }
}

Quest2314 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2314(qm: qm))
   }
   return (Quest2314) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}