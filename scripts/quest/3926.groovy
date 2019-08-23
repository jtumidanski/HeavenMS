package quest


import scripting.quest.QuestActionManager

class Quest3926 {
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
            int c = 0

            for (int i = 0; i < 4; i++) {
               if (qm.getQuestProgress(3926, i) == 1) {
                  c++
               }
            }

            if (c == 4) {
               qm.sendNext("You delivered all the jewels, well done!")
               qm.gainExp(6500)
               qm.forceCompleteQuest()
            } else {
               qm.sendNext("Have you brought all the jewels from the Red Scorpions? They have to be delivered to the Residential areas of the Sand Bandits.")
            }

            qm.dispose()
         }
      }
   }
}

Quest3926 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3926(qm: qm))
   }
   return (Quest3926) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}