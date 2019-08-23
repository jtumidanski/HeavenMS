package quest


import scripting.quest.QuestActionManager

class Quest3311 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
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
            if (qm.getQuestProgress(3311, 0) == 1 && qm.getQuestProgress(3311, 1) == 1) {
               qm.sendNext("Hmm, so the Alcadno doctor wrote something about researching some vanguardist Neo Huroid machine, that could beat by far the existing one, and was about to prepare the last steps of his rehearsal? We don't have a word about him for about three weeks now, something must have gone wrong...")
               qm.gainExp(60000)
               qm.forceCompleteQuest()
            } else {
               qm.sendNext("Found nothing yet? Please check out Dr. De Lang's house properly, something there may give out a clue about what is going on.")
            }

            qm.dispose()
         }
      }
   }
}

Quest3311 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3311(qm: qm))
   }
   return (Quest3311) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}