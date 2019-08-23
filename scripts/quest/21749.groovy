package quest


import scripting.quest.QuestActionManager

class Quest21749 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            qm.sendNext("So we have lost #btwo seal stones#k so far, from the neighboring areas of #rOrbis#k and #rMu Lung#k... Things are starting to get out of control, it seems.")
         } else if (status == 1) {
            qm.sendNext("Aran, your next objective will be to use the #btime gate to Ellin#k again. This time you will be retrieving the long lost #rSeal Stone of Ellin Forest#k. According to informations our network have gathered, #b#p2131002##k of that time have a clue about that gem, #rfind her#k. Please be successful on this task, our world is relying on you more than ever!")
         } else {
            qm.gainExp(500)
            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

Quest21749 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21749(qm: qm))
   }
   return (Quest21749) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}