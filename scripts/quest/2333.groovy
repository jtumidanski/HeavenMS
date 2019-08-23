package quest


import scripting.quest.QuestActionManager

class Quest2333 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || (mode == 0 && status == 0)) {
         qm.dispose()
         return
      } else if (mode == 0) {
         status--
      } else {
         status++
      }


      if (status == 0) {
         qm.sendAcceptDecline("Please help me!")
      } else if (status == 1) {
         qm.sendNext("The #bPrime Minister#k is the one who plotted all this! Oh no! Here he comes...")
      } else if (status == 2) {
         qm.forceStartQuest()
         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || (mode == 0 && status == 0)) {
         qm.dispose()
         return
      } else if (mode == 0) {
         status--
      } else {
         status++
      }


      if (status == 0) {
         qm.sendNext("Hurray! #b#h ##k you defeated the #bPrime Minister#k.")
      } else if (status == 1) {
         qm.gainExp(15000)
         qm.forceCompleteQuest()
         qm.dispose()
      }
   }
}

Quest2333 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2333(qm: qm))
   }
   return (Quest2333) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}