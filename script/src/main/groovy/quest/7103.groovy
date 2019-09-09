package quest


import scripting.quest.QuestActionManager

class Quest7103 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.sendOk("Oh really. Do you need more time? I'm fully confident that you'll help me out before the Time Sphere is formed.")
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            qm.sendOk("Oh really. Do you need more time? I'm fully confident that you'll help me out before the Time Sphere is formed.")
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            qm.sendYesNo("Only thing we have to do now ...is to make #o8500002# disappear forever... are you ready?")
         } else if (status == 1) {
            qm.sendNext("I'll explain to you what you need to do from here on out. \r\nTo enter the power-generating room, you'll need to pass either #bForgotten Passage#k or the #bWarped Passage#k. Once you defeat whichever monster that is guarding the passage, you can obtain #b#t4031172:##k, which is needed to enter the power-generating room.")
         } else if (status == 2) {
            qm.sendNextPrev("Then enter the room through the door in the middle. It's going to be MUCH quieter than you imagined. The Time Sphere should be hidden in a state undetectable in our eyes... but if you seal up the crack in dimension, the #o8500002#, panicking because its exit route is sealed up, will make its appearance there.")
         } else if (status == 3) {
            if (!qm.haveItem(4031179, 1)) {
               if (!qm.canHold(4031179, 1)) {
                  qm.sendOk("Please have an #rETC slot available#k to start this quest.")
                  qm.dispose()
                  return
               }

               qm.gainItem(4031179, (short) 1)
            }

            qm.sendAcceptDecline("Drop the #b#t4031179:##k that I returned to you to seal up whatever crack you see that #o8500002# may have used to enter this dimension in the first place. Then it'll come out of the Time Sphere and show everyone its true appearance. Please, please kill it and then come back. \r\n\r\nCollect #r1 #t4031172:##k\r\nEliminate #r#o8500001##k")
         } else if (status == 4) {
            qm.forceStartQuest()
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest7103 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest7103(qm: qm))
   }
   return (Quest7103) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}