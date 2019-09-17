package quest


import scripting.quest.QuestActionManager

class Quest2322 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (type == 1 && mode == 0) {
            status -= 2
         } else {
            qm.sendNext("Really? Is there another way you can penetrate the castle? If you don't know of one, then just come see me.")
            qm.dispose()
            return
         }
      }
      if (status == 0) {
         qm.sendYesNo("Like I told you, just breaking the barrier cannot be a cause for celebration. That's because our castle for the Kingdom of Mushroom completely denies entry of anyone outside our kingdom, so it'll be hard for you to do that. Hmmm... to figure out a way to enter, can you...investigate the outer walls of the castle first?")
      } else if (status == 1) {
         qm.sendNext("Walk past the Mushroom Forest and when you reach the #bSplit Road of Choice#k, just walk towards the castle. Good luck.")
      } else if (status == 2) {
         //qm.forceStartQuest();
         //qm.forceStartQuest(2322, "1");
         qm.gainExp(11000)
         qm.sendOk("Good job navigating through the area.")
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
         qm.sendOk("Hmmm I see... so they have completely shut off the entrance and everything.")
      } else if (status == 1) {
         qm.forceCompleteQuest()
         qm.gainExp(11000)
         qm.sendOk("Good job navigating through the area.")
      } else if (status == 2) {
         qm.dispose()
      }
   }
}

Quest2322 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2322(qm: qm))
   }
   return (Quest2322) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}